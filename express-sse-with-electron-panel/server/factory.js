getRandom = function (list) {
  const copy = [].concat(list);
  return copy[Math.floor(Math.random() * copy.length)];
};

class Factory {
  constructor() {
    this.connectionPool = [];
    this.list = [];
  }

  addListener(req, res) {
    res.writeHead(200, {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive'
    });
    this.connectionPool.push(res);

    req.connection.addListener("close", () => {
      this.connectionPool.splice(this.list.indexOf(res), 1);
    }, false);
    const retval = { type: 'list', data: this.list };
    res.write("data:" + JSON.stringify(retval) + "\n\n");

    console.log('new!!');
  }

  add() {
    const organisations = [
      "reactivity", "front", "back", "design", "test", "doc"
    ];
    const titles = ["Make love", "To be on the verge of something", "have fun", "be inspired", "More creative", "beers"];
    const users = ["fclety", "drouet", "cazelar", "qlevaslot", "harmio"];
    const categories = ["bug", "feature", "opti", "archi"];
    const colors = ["blue", "green", "pinklight", "magenta"];
    const descriptions = ["Minions ipsum duis aliquip wiiiii duis chasy. Ut tempor tank yuuu!",
      " Bappleees labore potatoooo bappleees ti aamoo!",
      " Veniam bappleees voluptate eiusmod jeje irure tatata bala tu bananaaaa labore ut.",
      " Po kass nostrud duis ut hahaha nostrud bananaaaa dolor nisi uuuhhh. ",
      "Bee do bee do bee do officia tank yuuu! Ex bananaaaa consectetur qui ut wiiiii uuuhhh. "];
    const priority = ["low", "normal", "high", "megaSupaHigh", "weAreDead"];
    var retval = {
      "timestamp": Date.now(),
      "title": getRandom(titles),
      "group": { "type": "organization", "name": getRandom(organisations) },
      "categories": {
        "assignee": getRandom(users),
        "category": getRandom(categories),
        "color": getRandom(colors),
        "description": getRandom(descriptions),
        "priority": getRandom(priority)
      }
    }

    this.list.push(retval);

    const action = { type: 'add', data: retval };
    this.broadcast(action);
    console.log('add!!');
  }

  delete() {
    const action = { type: 'delete', data: this.list.pop() };
    this.broadcast(action);
    console.log('delete!!');
  }

  update() {
    const idx = parseInt(Math.random() * this.list.length);
    const sticky = this.list[idx];
    const time = new Date();
    sticky.title = `Updated at : ${time.getHours()} :  ${time.getMinutes()} : ${time.getSeconds()}`;

    const action = { type: 'update', data: sticky };
    this.broadcast(action);
    console.log('update!!');
  }

  broadcast(message) {
    const data = JSON.stringify(message);
    this.connectionPool.forEach((res) => {
      res.write("data:" + data + "\n\n");
    });
  }
}


module.exports = new Factory();