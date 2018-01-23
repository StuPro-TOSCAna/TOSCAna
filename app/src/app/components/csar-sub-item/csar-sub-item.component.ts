import {Router} from '@angular/router';
import {Component, Input, OnInit} from '@angular/core';
import {Csar} from '../../model/csar';
import {ActiveTransformation, RouteHandler} from '../../handler/route/route.service';
import {isNullOrUndefined} from 'util';

@Component({
    selector: 'app-csar-sub-item',
    templateUrl: './csar-sub-item.component.html',
    styleUrls: ['./csar-sub-item.component.scss']
})
export class CsarSubItemComponent implements OnInit {
    @Input() csar: Csar;
    activeTransformation: ActiveTransformation;

    constructor(public router: Router, private routeHandler: RouteHandler) {
    }

    ngOnInit() {
        this.routeHandler.transformations.subscribe(data => {
            this.activeTransformation = data;
            console.log(data);
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
