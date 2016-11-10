const http = require("http");
const fs = require("fs");
const express = require('express');
const app = express();
const Factory = require('./factory');

var allowCrossDomain = (req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
  res.header('Access-Control-Allow-Headers', 'Content-Type');

  next();
};

app.use(allowCrossDomain);
app.get('/', (req, res) => {
  const fileName = './server/index.html';
  fs.exists(fileName, (exists) => {
    if (exists) {
      fs.readFile(fileName, (error, content) => {
        if (error) {
          res.writeHead(500);
          res.end();
        } else {
          res.writeHead(200, { "Content-Type": "text/html" });
          res.end(content, "utf-8");
        }
      });
    } else {
      res.writeHead(404);
      res.end();
    }
  });
});

app.get('/stream', (req, res) => {
  Factory.addListener(req, res);
});

app.post('/add', (req, res) => {
  Factory.add();
  res.sendStatus(200);
});

app.put('/update', (req, res) => {
  Factory.update();
  res.sendStatus(200);
});

app.delete('/delete', (req, res) => {
  Factory.delete();
  res.sendStatus(200);
});

app.listen(80, () => {
  console.log('Example app listening on port 80!');
});
