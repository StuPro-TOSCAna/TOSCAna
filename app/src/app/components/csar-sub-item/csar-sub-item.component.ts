import {Router} from '@angular/router';
import {Component, Input, OnInit} from '@angular/core';
import {Csar} from '../../model/csar';
import {ActiveTransformation, RouteHandler} from '../../handler/route/route.service';
import {isNullOrUndefined} from 'util';
import {TransformationsProvider} from '../../providers/transformations/transformations.provider';
import {CsarProvider} from '../../providers/csar/csar.provider';

@Component({
    selector: 'app-csar-sub-item',
    templateUrl: './csar-sub-item.component.html',
    styleUrls: ['./csar-sub-item.component.scss']
})
export class CsarSubItemComponent implements OnInit {
    @Input() csar: Csar;
    activeTransformation: ActiveTransformation;

    constructor(private router: Router, private csarProvider: CsarProvider, private transformationProvider: TransformationsProvider,
                private routeHandler: RouteHandler) {
    }

    ngOnInit() {
        this.routeHandler.transformations.subscribe(data => {
            this.activeTransformation = data;
        });
    }

    deleteTransformation() {
        this.routeHandler.close();
        this.transformationProvider.deleteTransformation(this.csar.name, this.activeTransformation.platform).subscribe(() => {
            const transformation = this.csar.transformations.find(item => item.platform === this.activeTransformation.platform);
            const pos = this.csar.transformations.indexOf(transformation);
            this.csar.transformations.slice(pos, 1);
            this.csarProvider.updateCsar(this.csar);
        });
    }

    isActive(platform: string) {
        if (!isNullOrUndefined(this.activeTransformation)) {
            return this.activeTransformation.platform === platform
                && this.activeTransformation.csarId === this.csar.name;
        }
        return false;
    }

    gotoTransformation(platform: string) {
        this.routeHandler.openTransformation(this.csar.name, platform);
    }

}
