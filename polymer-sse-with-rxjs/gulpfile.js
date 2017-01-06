const gulp = require('gulp');
const browserSync = require('browser-sync').create();
const proxyMiddleware = require('http-proxy-middleware');
const historyApiFallback = require('connect-history-api-fallback');

const options = {
    target: 'http://localhost:8080',
    changeOrigin: true,
    pathRewrite: {
        '^/api': '/'
    }
};

// Watch scss AND html files, doing different things with each.
gulp.task('default', () => {
    // Serve files from the root of this project
    browserSync.init({
        server: {
            baseDir: "./",
            index: "index.html",
            middleware: [
                historyApiFallback(),
                proxyMiddleware('/api', options)
            ]
        }
    });
});

