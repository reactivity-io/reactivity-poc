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
        req.open('GET', 'http://localhost:8080/load/organizations', true);
        req.onreadystatechange = () => {
            if (req.readyState == 4) {
                if(req.status == 200) {
                    this.organisations = JSON.parse(req.responseText);
                } else {
                    alert("Erreur pendant le chargement de la page.\n");
                }
            }
        };
        req.send(null);
    }
}
// Register custom element definition using standard platform API
customElements.define(ShellOrganisations.is, ShellOrganisations);