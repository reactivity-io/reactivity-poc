const gulp = require('gulp');
const vinylSourceStream = require('vinyl-source-stream');
const vinylBuffer = require('vinyl-buffer');
const gulpSourcemaps = require('gulp-sourcemaps');
const gulpif = require('gulp-if');
const watchify = require('watchify');
const browserify = require('browserify');
var uglify = require('gulp-uglify');

// Build javascript files with browserify, reactify and babelify
gulp.task('compile', (watch) => {
  let plugin = [];
  if (watch) {
    plugin = [[watchify, { ignoreWatch: ['**/node_modules/**'] }]];
    process.env.NODE_ENV = 'development';
  } else {
    process.env.NODE_ENV = 'production';
  }
  const bundler = browserify({
    entries: "./panel/app/app.js",
    debug: true,
    cache: {},
    packageCache: {},
    plugin,
    transform: [["eslintify"], ["babelify", { "presets": ["es2015", "react", "stage-2"] }], ["loose-envify"]]
  });

  const rebundle = () => {
    console.log('-> bundling...');
    return bundler.bundle()
      .on('error', (err) => {
        console.error(err);
      })
      .pipe(vinylSourceStream('build.js'))
      .pipe(vinylBuffer())
      .pipe(gulpSourcemaps.init({ loadMaps: true }))
      .pipe(gulpif(!watch, uglify()))
      .pipe(gulpSourcemaps.write('./'))
      .pipe(gulp.dest('./panel/build'));
  };

  if (watch) {
    bundler.on('update', () => {
      rebundle();
    });
    bundler.on("time", (time) => {
      console.log("   bundling done in " + time + "ms");
    });
  }
  return rebundle();
});