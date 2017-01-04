class ShellOrganisations extends Polymer.Element {
    static get is() {
        return 's-organisations';
    }
    Organisations () {
        onSignIn = signIn;
    }
    static get config() {
        return {
            properties: {
                orgaRoute: {
                    type: Object,
                    observer: '_test'
                }
            },
            observers: [
                '_orgaChange(orgaRoute.id)',
            ]
        }
    }

    constructor() {
        super();
        this.organisations = [];
    }

    connectedCallback() {
        super.connectedCallback();
        //
        // const manager = {
        //     "init-orga": (e) => {
        //         this.setProperties({organisations: JSON.parse(e.data)});
        //     },
        //     "message": (e) => {
        //         this.setProperties({organisations: [JSON.parse(e.data)]});
        //     },
        //     "add-orga": (e) => {
        //         const data = JSON.parse(e.data);
        //         this.organisations.push(data);
        //         this.setProperties({organisations: this.organisations});
        //     },
        //     "delete-orga": (e) => {
        //         const data = JSON.parse(e.data);
        //         this.organisations.pop();
        //         this.setProperties({organisations: this.organisations});
        //     }
        // };
        //
        // //Open the sse connection + create observable for each property of the given manager
        // const OrgaManager = new EventSourceManager("http://localhost:8080/load/organizations", manager);
        //
        // //Can subscribse to the "Open" EventSource's event
        // OrgaManager.sourceObservable.subscribe((e) => {
        //     console.log('%c new Subscriber', 'font-size:22px;text-shadow: 0 0 3px #FF0000, 0 0 5px rgb(232, 52, 90);');
        // });

        var req = new XMLHttpRequest();
        req.open('GET', 'http://localhost:8080/load/organizations', true);
        var that = this;
        req.onreadystatechange = (aEvt) => {
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
    _test(route) {
        console.log(route);
    }
    _orgaChange(id) {
        console.log('%c orgaId : ' ,'color:red', id);
    }

    openOrga(e) {
        console.log('%c orgaId : ' ,'color:red', e.target.routeData);
    }
}
// Register custom element definition using standard platform API
customElements.define(ShellOrganisations.is, ShellOrganisations);