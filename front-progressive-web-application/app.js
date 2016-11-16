const express = require('express'),
  sse = require('./server/sse');

const app = express(),
  connections = [],
  items = [];

// Static server
app.use('/', express.static(__dirname + '/build/unbundled'));

// Enable Server Send Events
app.use(sse);

/**
 * Classic GET endpoint
 * Update connected clients
 */
app.get('/item', (req, res) => {
  if (req.query.i) {
    items.push(req.query.i);
    for (const connection of connections) {
      connection.sseSend(items);
    }
    res.sendStatus(200)
  } else {
    res.sendStatus(400);
  }
});

app.get('/cache-first/:id', (req, res) => {
  res.send({
    id: req.params.id,
    timestamp: new Date(),
    type: 'cache-first',
  });
});

app.get('/network-first/:id', (req, res) => {
  res.send({
    id: req.params.id,
    timestamp: new Date(),
    type: 'network-first',
  });
});

/**
 * Register a new connection
 */
app.get('/stream', (req, res) => {
  res.sseSetup();
  res.sseSend(items);
  connections.push(res)
});

app.listen(3000, () => {
  console.log('Listening on port 3000...');
});