import {log} from 'util';
import {Injectable} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/delay';
import 'rxjs/add/operator/mergeMap';
import 'rxjs/add/operator/materialize';
import 'rxjs/add/operator/dematerialize';

@Injectable()
export class FakeBackend implements HttpInterceptor {

    constructor() {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return Observable.of(null).mergeMap(() => {
            log(request.url);
            // authenticate
            if (request.url.endsWith('api/csars') && request.method === 'GET') {
                console.log('blob');

                let body = {
                    _embedded: {
                        csar: [{
                            name: 'test'
                        }]
                    }
                };
                return Observable.of(new HttpResponse({status: 200, body: body}));
            }

            // // get users
            // if (request.url.endsWith('/api/users') && request.method === 'GET') {
            //     // check for fake auth token in header and return users if valid, this security is implemented server side in a real application
            //     if (request.headers.get('Authorization') === 'Bearer fake-jwt-token') {
            //         return Observable.of(new HttpResponse({ status: 200, body: users }));
            //     } else {
            //         // return 401 not authorised if token is null or invalid
            //         return Observable.throw('Unauthorised');
            //     }
            // }

            // get user by id
            if (request.url.endsWith('/api/platforms') && request.method === 'GET') {
                log('get csar transformations');
                // find user by id in users array
                let body = {
                    _embedded: {
                        platform: [{
                            id: 'kubernetes',
                            name: 'Kubernetes'
                        }, {
                            id: 'cloud-foundry',
                            name: 'CloudFoundry'
                        }]
                    }
                };
                return Observable.of(new HttpResponse({status: 200, body: body}));
            }

            // get transformation by id
            if (request.url.match(/\/api\/csars\/.+(?=\/transformation)\/transformations(\/.+){0}$/) && request.method === 'GET') {
                log('get csar transformations');
                // find user by id in users array
                let urlParts = request.url.split('/');
                let id = urlParts[urlParts.length - 2];
                console.log(id);
                let body = null;
                if (id === 'test') {
                    body = {
                        _embedded: {
                            transformation: [{
                                platform: 'kubernetes',
                                progress: '0',
                                status: 'READY'
                            }, {
                                platform: 'cloud-foundry',
                                progress: '0',
                                status: 'READY'
                            }]
                        }
                    };
                }
                return Observable.of(new HttpResponse({status: 200, body: body}));
            }
            // get user by id
            if (request.url.match(/\/api\/csars\/.+(?=\/transformation)\/transformations\/.+/) && request.method === 'GET') {
                // find user by id in users array
                let urlParts = request.url.split('/');
                let id = urlParts[urlParts.length - 3];
                console.log(id);
                let body = null;
                if (id === 'test') {
                    body = {
                        platform: 'cloud-foundry',
                        progress: '0',
                        status: 'READY'
                    };
                }
                return Observable.of(new HttpResponse({status: 200, body: body}));
            }

            // pass through any requests not handled above
            return next.handle(request);

        })

        // call materialize and dematerialize to ensure delay even if an error is thrown (https://github.com/Reactive-Extensions/RxJS/issues/648)
            .materialize()
            .delay(0)
            .dematerialize();
    }
}

export let fakeBackendProvider = {
    // use fake backend in place of Http service for backend-less development
    provide: HTTP_INTERCEPTORS,
    useClass: FakeBackend,
    multi: true
};
