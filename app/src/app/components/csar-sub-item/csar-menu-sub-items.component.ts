import {Router} from '@angular/router';
import {Component, Input, OnInit} from '@angular/core';
import {Csar} from '../../model/csar';
import {RouteHandler, TransformationOpen} from '../../services/route.service';
import {ClientsTransformationsService} from '../../services/transformations.service';
import {ClientCsarsService} from '../../services/csar.service';
import {TransformationResponse} from '../../api';
import StateEnum = TransformationResponse.StateEnum;
import {MessageService} from '../../services/message.service';

@Component({
    selector: 'app-csar-sub-item',
    templateUrl: './csar-menu-sub-items.component.html',
    styleUrls: ['./csar-menu-sub-items.component.scss']
})
export class CsarMenuSubItemsComponent implements OnInit {
    @Input() csar: Csar;
    viewState: TransformationOpen;
    activePlatform = '';
    stateEnum = StateEnum.TRANSFORMING;
    constructor(private messageService: MessageService, private router: Router, private csarProvider: ClientCsarsService,
                private transformationProvider: ClientsTransformationsService,
                private routeHandler: RouteHandler) {
    }

    ngOnInit() {
        this.routeHandler.viewState.subscribe(data => {
            if (data instanceof TransformationOpen) {
                this.viewState = data;
                if (this.viewState.csarId === this.csar.name) {
                    this.activePlatform = this.viewState.platform;
                }
            } else {
                this.activePlatform = '';
            }
        });
    }

    deleteTransformation(platform: string) {
        this.transformationProvider.deleteTransformation(this.csar.name, platform).subscribe(() => {
            const transformation = this.csar.transformations.find(item => item.platform === platform);
            const pos = this.csar.transformations.indexOf(transformation);
            this.csar.transformations.splice(pos, 1);
            this.csarProvider.updateCsar(this.csar);
            this.routeHandler.openCsar(this.csar.name);
        }, err => this.messageService.addErrorMessage('Failed to delete transformation'));
    }

    gotoTransformation(platform: string) {
        this.routeHandler.openTransformation(this.csar.name, platform);
    }

}
