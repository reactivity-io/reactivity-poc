class ShellDashboard extends Polymer.Element {
    static get is() {
        return 's-dashboard'
    }

    static get config() {
        return {
            properties: {
                page: {
                    type: String,
                    reflectToAttribute: true,
                    observer: '_pageChanged'
                }
            },
            observers: [
                '_routePageChanged(routeData.page)',
            ],
        }
    }

    _routePageChanged(page) {
        this.page = page || 'organisations';
    }

    _pageChanged(page) {
        // Load page import on demand. Show 404 page if fails
        var resolvedPageUrl = this.resolveUrl(`../pages/${page}/${page}.html`);
        Polymer.Utils.importHref(resolvedPageUrl, null, this._showPage404, true);
    }

    _showPage404() {
        this.page = 'view-404';
    }
}
customElements.define(ShellDashboard .is, ShellDashboard);