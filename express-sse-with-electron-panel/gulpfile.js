const gulp = require('gulp');
require('require-dir')('gulp-tasks');

gulp.task('default', gulp.parallel('css', 'compile'));
