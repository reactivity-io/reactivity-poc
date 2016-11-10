var gulp = require('gulp');
var del = require('del');

gulp.task('clean', (cb) => {
  del([global.config.build.rootDirectory], {dot: true})
    .then(() => {
      cb();
    });
});