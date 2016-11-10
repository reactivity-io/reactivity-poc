const gulp = require('gulp');
const project = require('./polymer-build-extend.js');
const browserSync = require('browser-sync').create();
const historyApiFallback = require('connect-history-api-fallback');

// Watch scss AND html files, doing different things with each.
gulp.task('serve', function () {
  // Serve files from the root of this project
  browserSync.init({
    server: {
      baseDir: "./build/unbundled/",
      index: "index.html",
      middleware: [historyApiFallback()]
    },
  });
});


// The source task will split all of your source files into one
// big ReadableStream. Source files are those in src/** as well as anything
// added to the sourceGlobs property of polymer.json.
// Because most HTML Imports contain inline CSS and JS, those inline resources
// will be split out into temporary files. You can use gulpif to filter files
// out of the stream and run them through specific tasks. An example is provided
// which filters all images and runs them through imagemin
function source() {
  return project.splitSource()
  // Add your own build tasks here!
    .pipe(project.rejoin()); // Call rejoin when you're finished
}

// The dependencies task will split all of your bower_components files into one
// big ReadableStream
// You probably don't need to do anything to your dependencies but it's here in
// case you need it :)
function dependencies() {
  return project.splitDependencies()
    .pipe(project.rejoin());
}

// Clean the build directory, split all source and dependency files into streams
// and process them, and output bundled and unbundled versions of the project
// with their own service workers
gulp.task('build', gulp.series([
  project.merge(source, dependencies),
  project.serviceWorker
]));