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
