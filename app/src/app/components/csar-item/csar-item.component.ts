import {Component, Input, OnInit} from '@angular/core';
import 'rxjs/add/observable/combineLatest';
import {Csar} from '../../model/csar';
import {ClientCsarsService} from '../../services/csar.service';
import {RouteHandler} from '../../services/route.service';
import {isNullOrUndefined} from 'util';
import {ViewState} from '../../model/view-states';

@Component({
    selector: 'app-csar-item',
    templateUrl: './csar-item.component.html',
    styleUrls: ['./csar-item.component.scss']
})
export class CsarItemComponent implements OnInit {
    @Input() csar: Csar;
    viewState: ViewState;
    csarViewActive = false;


    constructor(private routeHandler: RouteHandler, private csarProvider: ClientCsarsService) {
    }

    public open() {
        this.routeHandler.openCsar(this.csar.name);
    }

    newTransformation() {
        this.routeHandler.setUpCsar(this.csar.name);
        this.routeHandler.newTransformation(this.csar.name);
    }

    deleteCsar() {
        this.routeHandler.closeCsar();
        this.csarProvider.deleteCsar(this.csar.name);
    }

    ngOnInit() {
        // subscribe on the view state to mark this csar item as active
        // if the csar view or a transformation view with this csar as parent is active
        this.routeHandler.viewState.subscribe(data => {
            if (!isNullOrUndefined(data)) {
                this.viewState = data;
                this.csarViewActive = this.viewState.csarId === this.csar.name;
            }
        });
    }
}
