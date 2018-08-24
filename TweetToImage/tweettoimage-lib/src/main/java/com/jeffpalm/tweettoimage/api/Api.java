package com.jeffpalm.tweettoimage.api;

import android.util.Base64;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

public final class Api {

  private final Log log = new Log(this);
  private final Requests requests;
  private final Urls urls;
  private final BackgroundFactory backgroundFactory = new BackgroundFactoryImpl();
  private final JSONObjectSerializer jsonObjectSerializer = new JSONObjectSerializer();

  @Inject
  public Api(Requests requests, Urls urls) {
    this.requests = requests;
    this.urls = urls;
  }

  public BackgroundFactory getBackgroundFactory() {
    return backgroundFactory;
  }

  public void getBackgrounds(final GetBackgroundsHandler handler) {
    String url = urls.getCreateBackgroundsUrl();
    requests.request(url,
        Collections.emptyMap(),
        Requests.RequestMethod.GET,
        (JSONObject response) -> {
          GetBackgroundsResult res = createResponse(response, handler);
          if (res != null) {
            handler.onSuccess(res, jsonObjectSerializer.serialize(response));
          } else {
            handler.onError(new RuntimeException("No result"));
          }
        },
        handler::onError);
  }

  public GetBackgroundsResult getBackgrounds(String serializedBackgrounds) {
    JSONObject o = jsonObjectSerializer.deserialize(serializedBackgrounds);
    if (o == null) {
      log.i("No JSON from input string: %s", serializedBackgrounds);
      return null;
    }
    try {
      return createResponseInternal(o);
    } catch (JSONException e) {
      log.e(e, "creating GetBackgrounds response");
    }
    return null;
  }

  private GetBackgroundsResult createResponse(JSONObject o, GetBackgroundsHandler handler) {
    try {
      return createResponseInternal(o);
    } catch (JSONException e) {
      log.e(e, "creating GetBackgrounds response");
      handler.onError(e);
    }
    return null;
  }

  private GetBackgroundsResult createResponseInternal(JSONObject o) throws JSONException {
    ImmutableList.Builder<GetBackgroundsResult.Background> backgroundsBuilder = ImmutableList
        .builder();
    JSONArray backgrounds = o.getJSONArray("backgrounds");
    for (int i = 0; i < backgrounds.length(); i++) {
      JSONObject background = backgrounds.getJSONObject(i);
      String id = background.getString("id");
      String filename = background.getString("filename");
      String fullImageUrl = background.getString("full_image");
      String largeImageUrl = background.getString("large_image");
      String previewImageUrl = background.getString("preview_image");
      backgroundsBuilder.add(new GetBackgroundsResultImpl.BackgroundImpl(id,
          filename,
          fullImageUrl,
          largeImageUrl,
          previewImageUrl));
    }
    return new GetBackgroundsResultImpl(backgroundsBuilder.build());
  }

  public void getTemplates(final GetTemplatesHandler handler) {
    String url = urls.getCreateTemplatesUrl();
    requests.request(url,
        Collections.emptyMap(),
        Requests.RequestMethod.GET,
        (JSONObject response) -> {
          GetTemplatesResult res = createResponse(response, handler);
          if (res != null) {
            handler.onSuccess(res);
          } else {
            handler.onError(new RuntimeException("No result"));
          }
        },
        handler::onError);
  }

  private GetTemplatesResult createResponse(JSONObject response, GetTemplatesHandler handler) {
    try {
      return createResponseThrowing(response);
    } catch (JSONException e) {
      log.e(e, "Parsing response: %s", response);
      handler.onError(e);
    }
    return null;
  }

  private GetTemplatesResult createResponseThrowing(JSONObject response) throws JSONException {
    ImmutableList.Builder<GetTemplatesResult.Template> templatesBuilder = ImmutableList.builder();
    GetTemplatesResult.Template defaultTemplate = GetTemplatesResultImpl.newTemplateInstance(
        response.getString("defaultTemplate"));
    JSONArray templatesArray = response.getJSONArray("templates");
    for (int i = 0; i < templatesArray.length(); i++) {
      String templateKey = templatesArray.getString(i);
      templatesBuilder.add(GetTemplatesResultImpl.newTemplateInstance(templateKey));
    }
    List<GetTemplatesResult.Template> templates = templatesBuilder.build();
    Assert.that(!templates.isEmpty());
    return new GetTemplatesResultImpl(templates, defaultTemplate);
  }

  private String maybePadHex(int val) {
    String s = Integer.toHexString(val);
    if (s.length() == 1) s = "0" + s;
    return s;
  }

  private Map<String, String> toParams(@Nullable Background background) {
    if (background == null) {
      return Collections.emptyMap();
    }
    if (background instanceof BackgroundFile) {
      BackgroundFile bf = (BackgroundFile) background;
      return ImmutableMap.of("backgroundFile", bf.getFileName(), "backgroundMode", "fit");
    }
    if (background instanceof BackgroundBitmap) {
      BackgroundBitmap bf = (BackgroundBitmap) background;
      return ImmutableMap.of("backgroundBitmap", bf.getBitmapString(), "backgroundMode", "fit");
    }
    if (background instanceof BackgroundColor) {
      BackgroundColor bc = (BackgroundColor) background;
      int color = bc.getColor();
      int r = (color >> 16) & 0xff;
      int g = (color >> 8) & 0xff;
      int b = (color) & 0xff;
      // TODO: For now, remove the alpha.
      String colorString = "#" + maybePadHex(r) + maybePadHex(g) + maybePadHex(b);
      return ImmutableMap.of("backgroundColor", colorString);
    }
    return Collections.emptyMap();
  }

  public void createJson(Status s,
                         @Nullable String creatorId,
                         String templateKey,
                         @Nullable Background background,
                         final CreateJsonHandler handler) {
    ImmutableMap.Builder<String, String> paramsBuilder = ImmutableMap.<String, String>builder().put(
        "id",
        String.valueOf(s.getId())).put("text", s.getText()).put("created_at",
        s.getCreatedAt().toString()).put("screen_name", s.getUser().getScreenName()).put("name",
        s.getUser().getName()).put("template", templateKey).put("force", "true").put("image_url",
        s.getUser().getOriginalProfileImageURL());
    if (!Strings.isNullOrEmpty(creatorId)) {
      paramsBuilder.put("creator_id", creatorId);
    }
    paramsBuilder.putAll(toParams(background));
    ImmutableMap<String, String> params = paramsBuilder.build();
    String url = urls.getCreateJsonUrl();
    log.d("url: %s", (Object) url);
    log.d("params: %s", params);
    log.d("background: %s", background);
    requests.request(url, params, Requests.RequestMethod.POST, (JSONObject response) -> {
      CreateJsonResult res = createResponse(response, handler);
      if (res != null) {
        handler.onSuccess(res);
      } else {
        handler.onError(new RuntimeException("No result"));
      }
    }, handler::onError);
  }

  private CreateJsonResult createResponse(JSONObject o, CreateJsonHandler handler) {
    String src;
    try {
      src = o.getString("src");
    } catch (JSONException e) {
      log.e("Cannot get src");
      handler.onError(new IllegalArgumentException(("no src in response")));
      return null;
    }

    final String data = src;
    return () -> {
      String imageData = data.split(",")[1];
      return Base64.decode(imageData, Base64.DEFAULT);
    };
  }

  public interface CreateJsonHandler {
    void onSuccess(CreateJsonResult result);

    void onError(Throwable error);
  }

  public interface GetBackgroundsHandler {
    void onSuccess(GetBackgroundsResult result, @Nullable String serializableBackgrounds);

    void onError(Throwable error);
  }

  public interface GetTemplatesHandler {
    void onSuccess(GetTemplatesResult result);

    void onError(Throwable error);
  }

}
