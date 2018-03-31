import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {CsarOpen, InputFormOpen, TransformationOpen, ViewState} from '../model/view-states';
import {Observable} from 'rxjs/Observable';

/**
 * Manages the state of the current opened view
 * if the view changes it notifies all view state change subscribers so they can react
 */
@Injectable()
export class RouteHandler {
    _viewState: BehaviorSubject<ViewState>;
    inputLazyLoad = false;
    private dataStore: {
        viewState: ViewState;
    };

    constructor(private router: Router) {
        this.dataStore = {viewState: null};
        this._viewState = new BehaviorSubject<ViewState>(this.dataStore.viewState);
    }

    get viewState(): Observable<ViewState> {
        return this._viewState.asObservable();
    }

    private updateViewStateSubject() {
        this._viewState.next(Object.assign({}, this.dataStore).viewState);
    }

    public openCsarView(csarId: string) {
        this.router.navigate(['csar', csarId]);
        this.dataStore.viewState = new CsarOpen(csarId);
        this.updateViewStateSubject();
    }

    public openTransformationCreator(csarId: string) {
        this.router.navigate(['/new', csarId]);
    }

    public openTransformationView(csarId: string, platform: string) {
        this.router.navigate(['/transformation', csarId, platform]);
        this.dataStore.viewState = new TransformationOpen(csarId, platform);
        this.updateViewStateSubject();
    }

    public openTransformationInputs(csarId: string, platform: string) {
        this.inputLazyLoad = false;
        this.dataStore.viewState = new InputFormOpen(csarId, platform);
        this.updateViewStateSubject();
        this.router.navigate(['/inputs', csarId, platform]);
    }

    /**
     * notifies the sidebar that the given csar item should be opened and highlighted
     * @param {string} csarId
     */
    public openCsarItem(csarId: string) {
        const newState = new CsarOpen(csarId);
        if (this.dataStore.viewState !== newState) {
            this.dataStore.viewState = newState;
            this.updateViewStateSubject();
        }
    }

    public close() {
        this.router.navigate(['/']);
    }

    public closeCsar() {
        this.dataStore.viewState = null;
        this.updateViewStateSubject();
    }
}
