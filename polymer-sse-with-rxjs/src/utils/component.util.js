/**
 *
 * @param {Object} item - The employee who is responsible for the project.
 * @param {string} item.name - the name of the dom module.
 * @param {string} item.location - the url of the given component.
 * @param {string} item.attributes - attr of the futur component.
 * @param {HTMLElement} content the parent of the futur component.
 */
function ComponentLoader(item, content) {
  Polymer.Utils.importHref(
    item.location,
    //Success
    () => {
      const element = document.createElement(item.name);
      for (const attr in item.attributes) {
        if (item.attributes.hasOwnProperty(attr)) {
          element.setAttribute(attr, item.attributes[attr]);
        }
      }
      content.appendChild(element);
    }
    ,
    //Failure
    () => {
      const errorElement = document.createElement("div");
      errorElement.innerHTML = `error when loading ${item.name}`;
      content.appendChild(errorElement);
    }
  );
}
// <!--This is a route-->
// <!--super.connectedCallback();-->
// <!--//        if (this.route && this.route.path === '') {-->
// <!--//          this.route.path = '/orga';-->
// <!--//          this.setProperties({ route: this.route });-->
// <!--//        }-->
// <!--//        ComponentLoader(ManageRoute(this.route.path), this.$.content);-->
// <!--//-->
// <!--//        document.addEventListener('routeChange', (e) => {-->
// <!--//          this.route.path = `${this.route.path}${e.detail.page}`;-->
// <!--//          this.setProperties({ route: this.route });-->
// <!--//        });-->
// <!--//-->
// <!--//        window.addEventListener('location-changed', () => {-->
// <!--//          ComponentLoader(ManageRoute(this.route.path), this.$.content);-->
// <!--//        });-->

