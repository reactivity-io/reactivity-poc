getRandom = function (list) {
  const copy = [].concat(list);
  return copy[Math.floor(Math.random() * copy.length)];
};

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
const organisations = [
  "reactivity", "reactivity-poc", "reactivity-doc", "personal",
];
class Factory {
  constructor() {
    this.connectionPool = [];
    this.cards = [this.generateArtifact()];
    this.organisations = [this.generateOrganisation()];
  }

  addListener(req, res) {
    res.writeHead(200, {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive'
    });
    this.connectionPool.push(res);

    req.connection.addListener("close", () => {
      this.connectionPool.splice(this.connectionPool.indexOf(res), 1);
    }, false);


    res.write('event: init-orga\n');
    res.write("data:" + JSON.stringify(this.organisations) + "\n\n");
  }

  addOrga() {
    const retval = this.generateOrganisation();
    this.organisations.push(retval);
    this.broadcast('add-orga', retval);
  }

  deleteOrga() {
    this.broadcast('delete-orga', this.organisations.pop());
  }

  addCard() {
    const retval = this.generateArtifact();
    this.cards.push(retval);
    this.broadcast('add-card', retval);
  }

  deleteCard() {
    this.broadcast('delete-card', this.cards.pop());
  }

  updateCard() {
    const card = this.cards[Math.floor(Math.random() * this.cards.length)];
    const time = new Date();
    card.title = `Updated at : ${time.getHours()} :  ${time.getMinutes()} : ${time.getSeconds()}`;
    this.broadcast('update-card', card);
  }

  broadcast(action, message) {
    const data = JSON.stringify(message);
    this.connectionPool.forEach((res) => {
      res.write('event: ' + action + '\n');
      res.write("data:" + JSON.stringify(message) + "\n\n");
    });
  }

  generateOrganisation() {

    const images = ['./src/reactivex.png', './src/spring.png', './src/couchbase.png', './src/polymer.png', './src/polymer.png', './src/polymer.png'];
    var retval = {
      "timestamp": Date.now(),
      "type": "organization",
      name: 'app-orga',
      location: 'src/components/app-orga.html',
      attributes: {
        name: getRandom(organisations),
        itemNumber: this.cards.length,
        creationDate: this.cards[0].timestamp,
        image: getRandom(images),
        description: getRandom(descriptions)
      }
    };
    return retval;
  }

  generateArtifact() {

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
    };
    return retval;
  }
}


module.exports = new Factory();