class ShellOrganisation extends Polymer.Element {
    static get is() {
        return 's-organisation';
    }

    connectedCallback() {
        super.connectedCallback();

        if(this.id) {
            var req = new XMLHttpRequest();
            req.open('GET', `http://localhost:3000/api/subscribe/${this.id}`, true);
            req.onreadystatechange = () => {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        this.data = JSON.parse(req.responseText);
                        var groups = _.groupBy(this.data, "event");


                        //TODO:DO something with that !!! {{info}}



                    } else {
                        alert("Erreur pendant le chargement de la page.\n");
                    }
                }
            };
            req.send(null);
        }
    }
}
// Register custom element definition using standard platform API
customElements.define(ShellOrganisation.is, ShellOrganisation);