class ShellOrganisations extends Polymer.Element {
    static get is() {
        return 's-organisations';
    }

    constructor() {
        super();
        this.organisations = [];
    }

    connectedCallback() {
        super.connectedCallback();
        var req = new XMLHttpRequest();
        req.open('GET', 'http://localhost:3000/api/load/organizations', true);
        req.onreadystatechange = () => {
            if (req.readyState == 4) {
                if(req.status == 200) {
                    this.organisations = JSON.parse(req.responseText);
                } else {
                    alert("Don't be silly launch the server !\n");
                }
            }
        };
        req.send(null);
    }
}
// Register custom element definition using standard platform API
customElements.define(ShellOrganisations.is, ShellOrganisations);