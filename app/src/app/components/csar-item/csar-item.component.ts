import {Component, Input, OnInit} from '@angular/core';
import 'rxjs/add/observable/combineLatest';
import {Csar} from '../../model/csar';
import {BsModalRef} from 'ngx-bootstrap';
import {ClientCsarsService} from '../../services/csar.service';
import {RouteHandler, ViewState} from '../../services/route.service';
import {isNullOrUndefined} from 'util';

@Component({
    selector: 'app-csar-item',
    templateUrl: './csar-item.component.html',
    styleUrls: ['./csar-item.component.scss']
})
export class CsarItemComponent implements OnInit {
    @Input() csar: Csar;
    viewState: ViewState;
    active = false;
    config = {
        animated: true,
        keyboard: false,
        backdrop: true,
        ignoreBackdropClick: true
    };


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
        this.routeHandler.viewState.subscribe(data => {
            if (!isNullOrUndefined(data)) {
                this.viewState = data;
                this.active = this.viewState.csarId === this.csar.name;
            }
        });
    }
}
