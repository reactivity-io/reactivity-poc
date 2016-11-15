var del = require('del');

function clean() {
  return del([global.config.build.rootDirectory], {dot: true});
}

module.exports = clean;
