const gulp = require('gulp');
const browserSync = require('browser-sync').create();
const historyApiFallback = require('connect-history-api-fallback');

// Watch scss AND html files, doing different things with each.
gulp.task('serve', function () {
  // Serve files from the root of this project
  browserSync.init({
    server: {
      baseDir: "./",
      index: "index.html",
      middleware: [historyApiFallback()]
    },
  });
});
