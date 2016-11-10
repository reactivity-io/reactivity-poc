/**
 * @param {string} path - the name of the dom module.
 */
function ManageRoute(path) {
  const args = path.split('/');
  const model = {
    routes: {
      orga: {
        path: '/orga',
        name: 'orga-wrapper',
        location: 'src/my-organisations.html',
        routes: {
          add: {
            path: '/add',
            name: 'orga-wrapper',
            location: 'src/my-organisations.html',
          }
          ,
          reactivity: {
            path: '/reactivity',
            name: 'orga-wrapper',
            location: 'src/my-organisations.html',
            routes: {
              view: {
                path: '/view',
                name: 'app-card',
                location: 'src/components/app-card.html',
              },
            }
          },
        }
      },

      view: {
        path: '/perso',
        name: 'app-card',
        location: 'src/components/app-card.html'
      }
    }
  };

  const route = findChildRoute(model, args, 0);
  return route && route !== model ? route : model.routes.orga;
}

function findChildRoute(current, args, idx) {
  const arg = args[idx];
  if (arg === '' && idx < args.length) {
    return findChildRoute(current, args, idx + 1);
  }
  if (arg) {
    const nextChildren = current.routes[arg];
    if (nextChildren) {
      return findChildRoute(nextChildren, args, idx + 1);
    }
  }
  return current;
}