import {Component, Input, OnInit} from '@angular/core';
import 'rxjs/add/observable/combineLatest';
import {Csar} from '../../model/csar';
import {NewTransformationModalComponent} from '../new-transformation-modal/new-transformation-modal.component';
import {BsModalRef, BsModalService} from 'ngx-bootstrap';
import {CsarProvider} from '../../providers/csar/csar.provider';
import {RouteHandler} from '../../handler/route/route.service';

@Component({
    selector: 'app-csar-item',
    templateUrl: './csar-item.component.html',
    styleUrls: ['./csar-item.component.scss']
})
export class CsarItemComponent implements OnInit {
    @Input() csar: Csar;
    openItems: string[] = [];
    modalRef: BsModalRef;
    config = {
        animated: true,
        keyboard: false,
        backdrop: true,
        ignoreBackdropClick: true
    };


    constructor(private routeHandler: RouteHandler, private csarProvider: CsarProvider) {
    }

    public collapse(csarId: string) {
        this.routeHandler.toogleCsar(csarId);
    }

    isCollapsed(csarId: string) {
        return this.openItems.lastIndexOf(csarId) === -1;
    }

    newTransformation() {
        this.routeHandler.newTransformation(this.csar.name);
    }

    deleteCsar() {
        this.csarProvider.deleteCsar(this.csar.name);
        this.routeHandler.transformations.subscribe(data => {
            if (data.csarId === this.csar.name) {
                this.routeHandler.close();
            }
        });
    }

    ngOnInit() {
        this.routeHandler.csars.subscribe(data => {
            this.openItems = data;
        });
    }
}
