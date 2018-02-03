import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {isNullOrUndefined} from 'util';

export interface ActiveTransformation {
    csarId: string;
    platform: string;
}

@Injectable()
export class RouteHandler {
    _activeCsars: BehaviorSubject<string[]>;
    _activeTransformation: BehaviorSubject<ActiveTransformation>;
    _open: BehaviorSubject<boolean>;
    inputLazyLoad = false;
    private dataStore: {
        activeCsars: string[]
        activeTransformation: ActiveTransformation;
        open: boolean;
    };

    constructor(private router: Router) {
        this.dataStore = {activeCsars: [], activeTransformation: {csarId: '', platform: ''}, open: false};
        this._activeCsars = new BehaviorSubject<string[]>([]);
        this._activeTransformation = new BehaviorSubject<ActiveTransformation>(this.dataStore.activeTransformation);
        this._open = new BehaviorSubject<boolean>(false);
    }

    setUp() {
        const url = this.router.url;
        if (url.indexOf('transformation') !== -1) {
            const parts = url.split('/');
            const csar = parts[parts.length - 2];
            const platform = parts[parts.length - 1];
            this.addTransformation(csar, platform);
        } else if (url.indexOf('new') !== -1) {
            this.openSplit();
        } else if (url.indexOf('inputs') !== -1) {
            this.openSplit();
        }
    }

    get csars() {
        return this._activeCsars.asObservable();
    }

    get open() {
        return this._open.asObservable();
    }

    get transformations() {
        return this._activeTransformation.asObservable();
    }

    private updateCsarSubject() {
        this._activeCsars.next(Object.assign({}, this.dataStore).activeCsars);
    }

    private updateOpenSubject() {
        this._open.next(Object.assign({}, this.dataStore).open);
    }

    private updateTransformationSubject() {
        this._activeTransformation.next(Object.assign({}, this.dataStore).activeTransformation);
    }

    close() {
        this.router.navigate(['/']);
        this.dataStore.open = false;
        this.updateOpenSubject();
        if (isNullOrUndefined(this.dataStore.activeTransformation)) return;
        this.dataStore.activeCsars.splice(this.dataStore.activeCsars.indexOf(this.dataStore.activeTransformation.platform), 1);
        this.updateCsarSubject();
    }

    openTransformation(csarId: string, platform: string) {
        this.router.navigate(['/transformation', csarId, platform]);
        this.addTransformation(csarId, platform);
    }

    private addTransformation(csarId: string, platform: string) {
        if (this.dataStore.activeCsars.indexOf(csarId) === -1) {
            this.dataStore.activeCsars.push(csarId);
        }
        console.log(this.dataStore.activeTransformation);
        this.dataStore.activeTransformation.csarId = csarId;
        this.dataStore.activeTransformation.platform = platform;
        console.log(this.dataStore.activeTransformation);

        this.dataStore.open = true;

        this.updateCsarSubject();
        this.updateOpenSubject();
        this.updateTransformationSubject();
    }

    toogleCsar(csarId: string) {
        if (this.dataStore.activeCsars.indexOf(csarId) === -1) {
            this.dataStore.activeCsars.push(csarId);
        } else {
            this.dataStore.activeCsars.splice(this.dataStore.activeCsars.indexOf(csarId), 1);

        }
        this.updateCsarSubject();
    }

    newTransformation(csarId: string) {
        this.router.navigate(['/new', csarId]);
        this.openSplit();
    }

    private openSplit() {
        this.dataStore.open = true;
        this.updateOpenSubject();
    }

    openInputs(csarId: string, platform: string) {
        this.inputLazyLoad = false;
        this.router.navigate(['/inputs', csarId, platform]);
        this.dataStore.open = true;
        this.updateOpenSubject();
    }

}
