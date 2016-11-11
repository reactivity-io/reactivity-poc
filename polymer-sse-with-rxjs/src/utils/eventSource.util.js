class EventSourceManager {
  constructor(url, manager) {
    //Create an Observable for the EventSource Connection
    this.sourceObservable = Rx.Observable.create((observer) => {
      this.source = new EventSource(url);

      const onOpen = (e) => {
        observer.onNext(e);
        this.source.removeEventListener('open', onOpen, false);
      };

      const onError = (e) => {
        if (e.readyState === EventSource.CLOSED) {
          observer.onCompleted();
        } else {
          observer.onError(e);
        }
      };

      const onMessage = (e) => {
        observer.onNext(e);
      };

      this.source.addEventListener('open', onOpen, false);
      this.source.addEventListener('error', onError, false);
      this.source.addEventListener('message', onMessage, false);

      //Add listener on specific eventType
      this.addEventsObservables(manager);

      return () => {
        this.source.removeEventListener('error', onError, false);
        this.source.removeEventListener('message', onMessage, false);
        this.source.close();
      };
    });
  }

  static get source() {
    return this.source;
  }

  static get sourceObservable() {
    return this.sourceObservable;
  }

  /**
   * Create an observable for each property of the given manager
   * @param {Object} manager
   * @returns {void}
   */
  addEventsObservables(manager) {
    //for each property
    for (let eventType in manager) {
      if (manager.hasOwnProperty(eventType)) {

        //Create a new observable
        const observable = Rx.Observable.create((observer) => {
          const onMessage = (e) => {
            observer.onNext(e);
          };

          //listen the eventType on the EventSource
          this.source.addEventListener(eventType, onMessage, false);
        });

        //Subscribe the callback.
        observable.subscribe((e) => {
          manager[eventType](e);
        });
      }
    }
  }
}