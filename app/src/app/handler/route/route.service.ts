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

export class InputFormOpen extends TransformationOpen {
    constructor(csarId: string, platform: string) {
        super(csarId, platform);
    }
}

export class CsarOpen extends ViewState {
    constructor(csarId: string) {
        super(csarId);
    }
}

@Injectable()
export class RouteHandler {
    _viewState: BehaviorSubject<ViewState>;
    inputLazyLoad = false;
    v;
    private dataStore: {
        viewState: ViewState;
    };

    constructor(private router: Router) {
        this.dataStore = {viewState: null};
        this._viewState = new BehaviorSubject<ViewState>(this.dataStore.viewState);
    }

    get viewState() {
        return this._viewState.asObservable();
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
        this.dataStore.viewState = new InputFormOpen(csarId, platform);
        this.updateViewStateSubject();
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
