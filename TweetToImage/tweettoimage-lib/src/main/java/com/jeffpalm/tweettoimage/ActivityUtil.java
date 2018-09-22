package com.jeffpalm.tweettoimage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.jeffpalm.tweettoimage.api.Status;
import com.jeffpalm.tweettoimage.api.StatusImpl;
import com.jeffpalm.tweettoimage.api.TwitterUserImpl;
import com.jeffpalm.tweettoimage.prefs.Preferences;
import com.jeffpalm.tweettoimage.prefs.PreferencesUtil;
import com.jeffpalm.tweettoimage.util.Assert;
import com.jeffpalm.tweettoimage.util.ImageDownloader;
import com.jeffpalm.tweettoimage.util.Log;
import com.jeffpalm.tweettoimage.util.StatusAndTemplateKey;

import java.util.Date;

import javax.inject.Inject;

public final class ActivityUtil {

  private final static String TRUMP_IMAGE_URL = "https://pbs.twimg.com/" +
      "profile_images/874276197357596672/kUuht00m_normal.jpg";
  private final static String TRUMP_NAME = "Donald J. Trump";
  private final static String TRUMP_SCREEN_NAME = "realDonaldTrump";
  private final static String TRUMP_DESCRIPTION = "45th President of the " + "United States of "
      + "America\uD83C\uDDFA\uD83C\uDDF8";
  private static final long TRUMP_STATUS_ID = 1005586562959093760L;
  private final static int BACKGROUND_PREVIEW_TEXT_SIZE = 10;
  private final Log log = new Log(this);
  private final Activity context;
  private final TwimmageUser twimmageUser;
  private final LoginController loginController;
  private final TemplatesProvider templatesProvider;
  private final ImageDownloader imageDownloader;
  private final Options options;
  private final BackgroundsProvider backgroundsProvider;
  private final Class<? extends ListActivity> listActivityClass;
  private final Class<? extends SearchActivity> searchActivityClass;
  private final Class<? extends EditActivity> editActivityClass;
  private final Class<? extends MainActivity> mainActivityClass;
  private final Class<? extends DebugActivity> debugActivityClass;
  private Views views;
  private int showProgresses = 0;

  @Inject
  ActivityUtil(Activity context,
               TwimmageUser twimmageUser,
               LoginController loginController,
               TemplatesProvider templatesProvider,
               ImageDownloader imageDownloader,
               Options options,
               Class<? extends ListActivity> listActivityClass,
               Class<? extends SearchActivity> searchActivityClass,
               Class<? extends EditActivity> editActivityClass,
               Class<? extends MainActivity> mainActivityClass,
               Class<? extends DebugActivity> debugActivityClass,
               BackgroundsProvider backgroundsProvider) {
    this.context = context;
    this.twimmageUser = twimmageUser;
    this.loginController = loginController;
    this.templatesProvider = templatesProvider;
    this.imageDownloader = imageDownloader;
    this.options = options;
    this.listActivityClass = listActivityClass;
    this.searchActivityClass = searchActivityClass;
    this.editActivityClass = editActivityClass;
    this.mainActivityClass = mainActivityClass;
    this.debugActivityClass = debugActivityClass;
    this.backgroundsProvider = backgroundsProvider;
  }

  private static void hideKeyboard(Activity activity) {
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity
        .INPUT_METHOD_SERVICE);
    //Find the currently focused view, so we can grab the correct window
    // token from it.
    View view = activity.getCurrentFocus();
    //If no view currently has focus, create a new one, just so we can grab a
    // window token from it
    if (view == null) {
      view = new View(activity);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static <T extends View> T findViewById(View container, int id) {
    T view = container.findViewById(id);
    return Assert.notNull(view);
  }

  public boolean checkForLogin() {
    if (!isLoggedIn()) {
      Intent intent = new Intent(context, mainActivityClass);
      intent.putExtra(MainActivity.EXTRA_LOGIN, true);
      context.startActivity(intent);
      return true;
    }
    return false;
  }

  private boolean isLoggedIn() {
    return twimmageUser.isLoggedIn();
  }

  public void hideKeyboard() {
    hideKeyboard(context);
  }

  public void listenTo(WriterController writerController) {
    writerController.setListener(new WriterController.Listener() {
      @Override
      public void onStart() {
        showProgress(true);
      }

      @Override
      public void onStop() {
        showProgress(false);
      }

      @Override
      public void onError(Throwable t) {
        log.e(t, "Could not share: %s", t);
        Toast.makeText(context,
            String.format("Could not share: %s", t.getMessage()),
            Toast.LENGTH_LONG).show();
        showProgress(false);
      }
    });
  }

  public void showProgress(boolean show) {
    showProgresses += show ? 1 : -1;
    if (showProgresses > 0) {
      views.imageView.setVisibility(View.INVISIBLE);
      views.progressLoader.setVisibility(View.VISIBLE);
    } else {
      views.progressLoader.setVisibility(View.INVISIBLE);
      views.imageView.setVisibility(View.VISIBLE);
    }
  }

  private boolean isSpudtrooper() {
    return twimmageUser.isLoggedIn() && twimmageUser.getTwitterUser() != null && Strings
        .nullToEmpty(
        twimmageUser.getTwitterUser().getScreenName()).equals("spudtrooper");
  }

  public void onCreate() {
    class Helper {
      private void setUpNavigationView() {
        // TODO: Get these in a better way than findViewById.
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        ImageView navImageView = findViewById(headerLayout, R.id.nav_image_view);
        TextView nameText = findViewById(headerLayout, R.id.nav_name_text);
        TextView screenNameText = findViewById(headerLayout, R.id.nav_screen_name_text);
        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        if (twimmageUser.isLoggedIn()) {
          String url = twimmageUser.getTwitterUser().getOriginalProfileImageURL();
          imageDownloader.download(url, navImageView, toolbarImage);
          nameText.setText(twimmageUser.getTwitterUser().getName());
          nameText.setVisibility(View.VISIBLE);
          screenNameText.setVisibility(View.VISIBLE);
          // Don't set the toolbarImage visible, the DownloadTask will do that
        } else {
          nameText.setText("");
          screenNameText.setText("");
          nameText.setVisibility(View.INVISIBLE);
          screenNameText.setVisibility(View.INVISIBLE);
          toolbarImage.setVisibility(View.INVISIBLE);
        }
      }

      void addThreeDrawLines(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(context,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
        if (drawer != null) {
          drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
      }

      void setUpNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
          final int id = item.getItemId();
          if (id == R.id.nav_preferences_btn) {
            startPreferencesActivity();
            return true;
          }
          if (id == R.id.nav_home_btn) {
            startListActivity();
            return true;
          }
          if (id == R.id.nav_logout_btn) {
            logout();
            return true;
          }
          if (id == R.id.nav_reload_btn) {
            reload();
            return true;
          }
          if (id == R.id.nav_trump_btn) {
            startTrumpActivity();
            return true;
          }
          if (id == R.id.nav_search_btn) {
            startSearchActivity();
            return true;
          }
          if (id == R.id.nav_debug_btn) {
            startDebugActivity();
            return true;
          }
          return false;
        });

        MenuItem prefsButton = navigationView.getMenu().findItem(R.id.nav_preferences_btn);
        MenuItem homeButton = navigationView.getMenu().findItem(R.id.nav_home_btn);
        MenuItem logoutButton = navigationView.getMenu().findItem(R.id.nav_logout_btn);
        MenuItem searchButton = navigationView.getMenu().findItem(R.id.nav_search_btn);
        MenuItem trumpButton = navigationView.getMenu().findItem(R.id.nav_trump_btn);
        MenuItem reloadButton = navigationView.getMenu().findItem(R.id.nav_reload_btn);
        MenuItem debugButton = navigationView.getMenu().findItem(R.id.nav_debug_btn);
        TextView nameText = findViewById(navigationView.getHeaderView(0), R.id.nav_name_text);
        TextView screenNameText = findViewById(navigationView.getHeaderView(0),
            R.id.nav_screen_name_text);

        // Always on.
        homeButton.setVisible(true);
        searchButton.setVisible(true);

        trumpButton.setVisible(options.shouldShowTrump());

        // Show debug for debug builds or for Jeff.
        debugButton.setVisible(BuildConfig.DEBUG || isSpudtrooper());

        // Toggled when logged off.
        if (isLoggedIn()) {
          prefsButton.setVisible(true);
          homeButton.setTitle(R.string.home);
          logoutButton.setVisible(true);
          reloadButton.setVisible(true);
          nameText.setText(twimmageUser.getTwitterUser().getName());
          screenNameText.setText(context.getString(R.string.screen_name_template,
              twimmageUser.getTwitterUser().getScreenName()));
          nameText.setVisibility(View.VISIBLE);
          screenNameText.setVisibility(View.VISIBLE);
        } else {
          prefsButton.setVisible(false);
          homeButton.setTitle(R.string.log_in);
          logoutButton.setVisible(false);
          reloadButton.setVisible(false);
          nameText.setVisibility(View.INVISIBLE);
          screenNameText.setVisibility(View.INVISIBLE);
        }
      }
    }

    views = Views.newInstance(this);

    Helper helper = new Helper();

    helper.setUpNavigationView();
    if (options.shouldShowNagivationView()) {
      Toolbar toolbar = findViewById(R.id.toolbar);
      if (context instanceof AppCompatActivity) {
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        ((AppCompatActivity) context).getSupportActionBar().setDisplayShowHomeEnabled(true);
      }
      helper.addThreeDrawLines(toolbar);
      helper.setUpNavigationViewListener();
    }
  }

  public <T extends View> T findViewById(int id) {
    T view = context.findViewById(id);
    return Assert.notNull(view);
  }

  private void startPreferencesActivity() {
    context.startActivity(new Intent(context, PreferencesActivity.class));
  }

  private void startListActivity() {
    context.startActivity(new Intent(context, listActivityClass));
  }

  public void logout() {
    Intent intent = new Intent(context, mainActivityClass);
    intent.putExtra(MainActivity.EXTRA_LOGOUT, true);
    context.startActivity(intent);
    loginController.logoutUser();
  }

  private void reload() {
    backgroundsProvider.clear();
    templatesProvider.clear();
    context.finish();
    context.startActivity(context.getIntent());
  }

  private void startTrumpActivity() {
    TwitterUserImpl twitterUser = new TwitterUserImpl(TRUMP_NAME,
        TRUMP_SCREEN_NAME,
        TRUMP_IMAGE_URL,
        TRUMP_DESCRIPTION);
    Status status = new StatusImpl(TRUMP_STATUS_ID, "", new Date(), 1, 1, twitterUser);
    Intent intent = new Intent(context, editActivityClass);
    intent.putExtra(EditActivity.EXTRA_STATUS_AND_TEMPLATE_KEY,
        StatusAndTemplateKey.newInstance(status, templatesProvider.getDefaultTemplate()));
    intent.putExtra(EditActivity.EXTRA_SELECT_ALL, true);
    context.startActivity(intent);
  }

  public void startSearchActivity() {
    context.startActivity(new Intent(context, searchActivityClass));
  }

  private void startDebugActivity() {
    context.startActivity(new Intent(context, debugActivityClass));
  }

  public boolean getBooleanPreference(Preferences.Preference<Boolean> pref) {
    return PreferencesUtil.getBooleanPreference(context, pref);
  }

  public SharedPreferences getSharedPreferences() {
    return PreferencesUtil.getSharedPreferences(context);
  }

  public void setBackground(ViewPager viewPager, Background background) {
    if (background instanceof BackgroundColor) {
      viewPager.setBackground(new ColorDrawable(((BackgroundColor) background).getColor()));
    } else if (background instanceof BackgroundFile) {
      ((BackgroundFile) background).getLargeImage(context,
          image -> viewPager.setBackground(new BitmapDrawable(context.getResources(), image)));
    } else if (background instanceof BackgroundBitmap) {
      viewPager.setBackground(new BitmapDrawable(context.getResources(),
          ((BackgroundBitmap) background).getBitmap()));
    } else {
      viewPager.setBackgroundResource(0);
    }
  }

  /**
   * Interface for configuration the behavior between apps.
   */
  public interface Options {
    boolean shouldShowNagivationView();

    boolean shouldShowTrump();
  }

  /**
   * Caches views found in onCreate.
   */
  private final static class Views {
    final ProgressBar progressLoader;
    final ImageView imageView;

    private Views(ProgressBar progressLoader, ImageView imageView) {
      this.progressLoader = progressLoader;
      this.imageView = imageView;
    }

    static Views newInstance(ActivityUtil activityUtil) {
      return new Views(activityUtil.findViewById(R.id.progress_loader),
          activityUtil.findViewById(R.id.toolbar_image));
    }
  }


}
