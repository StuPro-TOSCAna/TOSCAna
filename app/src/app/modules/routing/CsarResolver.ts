import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {CsarProvider} from '../../providers/csar/csar.provider';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/skipWhile';
import 'rxjs/add/operator/take';
import {Csar} from '../../model/csar';
import {isNullOrUndefined} from 'util';

@Injectable()
export class CanActivateNew implements Resolve<Observable<Csar>> {

    constructor(private csarProvider: CsarProvider) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Csar> {
        console.log(this.csarProvider.getCount());
        let count = 1;
        if (this.csarProvider.getCount() === 0) {
            count = 2;
        }
        let res = this.csarProvider.csars.take(count).map(array => {
                console.log(route.params['csarId']);
                return array.find(item => item.name === route.params['csarId']);
            }
        );
        if (count === 2) {
            res = res.skipWhile(data => !isNullOrUndefined(data));
        }
        return res;
    }

}
