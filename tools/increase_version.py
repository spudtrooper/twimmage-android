"""
Increases the versionCode and versionName in gradle files.
"""
import argparse
import logging
import os
import re
import sys

class GradleProcessor(object):

  def __init__(self, dry_run):
    self._dry_run = dry_run

  def _IncreaseVersion(self, gradle_file):
    output = []
    with open(gradle_file) as f:
      for line in f:
        
        m = re.match(r'^(\s*versionCode\s*)(\d+)', line)
        if m:
          start = m.group(1)
          vc = int(m.group(2))
          new_vc = vc + 1
          line = '%s%d\n' % (start, new_vc)
          self._Info('Changing versionCode from %d to %d', vc, new_vc)
          output.append(line)
          continue
        
        m = re.match(r'^(\s*versionName "\d+\.\d+\.)(\d+)"', line)
        if m:
          start = m.group(1)
          vc = m.group(2)
          new_vc = ('%0' + str(len(vc)) + 'g') % (int(vc) + 1)
          line = '%s%s"\n' % (start, new_vc)
          self._Info('Changing versionName from %s to %s', vc, new_vc)
          output.append(line)
          continue
        
        output.append(line)
        
    return ''.join(output)
      
  def _IncreaseAndWrite(self, gradle_file):
    new_contents = self._IncreaseVersion(gradle_file)
    if self._dry_run:
      self._Info('Not writing to %s', gradle_file)
    else:
      self._Info('Writing to %s', gradle_file)
      with open(gradle_file, 'w') as f:
        f.write(new_contents)

  def _Info(self, tmpl, *args):
    logging.info(self._logging_prefix + tmpl, *args)

  def ProcessFiles(self, gradle_files):
    for i, gradle_file in enumerate(gradle_files):
      # TweetToImage/tweettoimage-lib/build.gradle -> tweettoimage-lib
      app_name = os.path.basename(os.path.dirname(gradle_file))
      self._logging_prefix = '[%d of %d : %s] ' % (i+1, len(gradle_files), app_name)
      self._IncreaseAndWrite(gradle_file)

def Main(argv):
  parser = argparse.ArgumentParser()
  parser.add_argument("-n", "--dry_run", type=bool, nargs='?', const=True,
                  help="Don't write files, just print actions")
  namespace = vars(parser.parse_args())
  processor = GradleProcessor(dry_run=namespace['dry_run'])
  processor.ProcessFiles([
    'TweetToImage/full-app/build.gradle',
    'TweetToImage/lite-app/build.gradle',
    'TweetToImage/test-app/build.gradle',
    'TweetToImage/tweettoimage-lib/build.gradle'
  ])

if __name__ == '__main__':
  logging.basicConfig(level=logging.INFO)
  Main(sys.argv)
