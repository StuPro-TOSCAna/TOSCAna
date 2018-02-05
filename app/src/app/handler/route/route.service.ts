import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

export interface ActiveTransformation {
    csarId: string;
    platform: string;
}

export class ViewState {
    csarId: string;

    constructor(csarId: string) {
        this.csarId = csarId;
    }
}

export class TransformationOpen extends ViewState {
    platform: string;

    constructor(csarId: string, platform: string) {
        super(csarId);
        this.platform = platform;
    }
}

export class CsarOpen extends ViewState {
    constructor(csarId: string) {
        super(csarId);
    }
}

@Injectable()
export class RouteHandler {
    _activeCsars: BehaviorSubject<string[]>;
    _viewState: BehaviorSubject<ViewState>;
    _open: BehaviorSubject<boolean>;
    inputLazyLoad = false;
    v;
    private dataStore: {
        activeCsars: string[]
        viewState: ViewState;
        open: boolean;
    };

    constructor(private router: Router) {
        this.dataStore = {activeCsars: [], viewState: null, open: false};
        this._activeCsars = new BehaviorSubject<string[]>([]);
        this._viewState = new BehaviorSubject<ViewState>(this.dataStore.viewState);
        this._open = new BehaviorSubject<boolean>(false);
    }

    setUp() {
        const url = this.router.url;
        // if (url.indexOf('transformation') !== -1) {
        //     const parts = url.split('/');
        //     const csar = parts[parts.length - 2];
        //     const platform = parts[parts.length - 1];
        //     this.addTransformation(csar, platform);
        // } else if (url.indexOf('new') !== -1) {
        //     this.openSplit();
        // } else if (url.indexOf('inputs') !== -1) {
        //     this.openSplit();
        // }
    }


    get open() {
        return this._open.asObservable();
    }

    get viewState() {
        return this._viewState.asObservable();
    }

    private updateCsarSubject() {
        this._activeCsars.next(Object.assign({}, this.dataStore).activeCsars);
    }

    private updateOpenSubject() {
        this._open.next(Object.assign({}, this.dataStore).open);
    }

    private updateViewStateSubject() {
        this._viewState.next(Object.assign({}, this.dataStore).viewState);
    }

    openCsar(csarId: string) {
        this.router.navigate(['csar', csarId]);
        this.dataStore.viewState = new CsarOpen(csarId);
        this.updateViewStateSubject();
    }

    newTransformation(csarId: string) {
        this.router.navigate(['/new', csarId]);
    }

    openTransformation(csarId: string, platform: string) {
        this.router.navigate(['/transformation', csarId, platform]);
        this.dataStore.viewState = new TransformationOpen(csarId, platform);
        this.updateViewStateSubject();
    }

    openInputs(csarId: string, platform: string) {
        this.inputLazyLoad = false;
        this.router.navigate(['/inputs', csarId, platform]);
    }

    setUpCsar(csarId: string) {
        const newState = new CsarOpen(csarId);
        if (this.dataStore.viewState !== newState) {
            this.dataStore.viewState = newState;
            this.updateViewStateSubject();
        }
    }

    close() {
        this.router.navigate(['/']);
    }

    closeCsar() {
        this.dataStore.viewState = null;
        this.updateViewStateSubject();
    }
}
