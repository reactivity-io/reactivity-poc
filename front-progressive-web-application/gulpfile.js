const gulp = require('gulp');
const gulpif = require('gulp-if');
const fs = require('fs');

// Keep the global.config above any of the gulp-tasks that depend on it
global.config = {
  build: {
    rootDirectory: 'build',
    bundledDirectory: 'bundled',
    unbundledDirectory: 'unbundled',
    // A bundled version will be vulcanized and sharded. An unbundled version will not have its files combined (this is
    // for projects using HTTP/2 server push).
    bundleType: 'both'
  },
  // Path to your service worker, relative to the build root directory
  serviceWorkerPath: 'service-worker.js',
  // Service Worker precache options based on https://github.com/GoogleChrome/sw-precache#options-parameter
  swPrecacheConfig: {
    staticFileGlobs: [
      '/index.html',
      '/manifest.json',
      '/bower_components/webcomponentsjs/webcomponents-lite.min.js',
      '/static/**/*.*',
    ],
    navigateFallback: '/index.html',
    importScripts: ['/bower_components/sw-toolbox/sw-toolbox.js'],
    runtimeCaching: [{
      urlPattern: /\/network-first\//,
      handler: 'networkFirst'
    }, {
      urlPattern: /\/cache-first\//,
      handler: 'cacheFirst',
      options: {
        cache: {
          maxEntries: 5,
          name: 'cache-first'
        }
      }
    //}, {
    //  urlPattern: /\/stream/,
    //  handler: 'sse',
    //  options: {
    //    cache: {
    //      maxEntries: 5,
    //      name: 'network-first'
    //    }
    //  }
    }],
  }
};

const project = require('./gulp-tasks/polymer-build-extend.js');
const clean = require('./gulp-tasks/clean.js');
const compile = require('./gulp-tasks/compile.js');

// The source task will split all of your source files into one big ReadableStream. Source files are those in src/** as
// well as anything added to the sourceGlobs property of polymer.json.
function source() {
  return project.splitSource()
    // Add your own build tasks here!
    //.pipe(gulpif('**/*.{png,gif,jpg,svg}', images.minify()))
    //.pipe(gulpif(/\.js$/, compile()))
    .pipe(project.rejoin()); // Call rejoin when you're finished
}

// The dependencies task will split all of your bower_components files into one big ReadableStream
function dependencies() {
  return project.splitDependencies()
    .pipe(project.rejoin());
}

// Clean the build directory, split all source and dependency files into streams and process them, and output bundled
// and unbundled versions of the project with their own service workers
gulp.task('build', gulp.series([
  project.merge(source, dependencies),
  project.serviceWorker
]));


const allBuildTasks = ['build'];

gulp.task('default', gulp.series([clean, allBuildTasks]));