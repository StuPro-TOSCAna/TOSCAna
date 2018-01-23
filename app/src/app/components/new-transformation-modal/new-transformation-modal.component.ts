import {Component, OnInit, ViewChild} from '@angular/core';
import {PlatformsProvider} from '../../providers/platforms/platforms.provider';
import {PlatformResponse} from '../../api';
import {TransformationsProvider} from '../../providers/transformations/transformations.provider';
import {Csar} from '../../model/csar';
import {ActivatedRoute} from '@angular/router';
import {CsarProvider} from '../../providers/csar/csar.provider';
import {Helper} from '../../helper/helper';
import {isNullOrUndefined} from 'util';
import {RouteHandler} from '../../handler/route/route.service';
import 'rxjs/add/observable/fromPromise';

@Component({
    selector: 'app-new-transformation-modal',
    templateUrl: './new-transformation-modal.component.html',
    styleUrls: ['./new-transformation-modal.component.scss']
})
export class NewTransformationModalComponent implements OnInit {
    @ViewChild('template')
    template;
    csar: Csar;
    platforms: PlatformResponse[];
    alreadyUsedPlatforms: PlatformResponse[];
    title: string;
    platform: string;
    platformSelected = false;
    selectPlatformString = 'Select Platform';
    enterInputsString = 'Enter Inputs';
    notNullOrUndefined = Helper.notNullOrUndefined;
    config = {
        animated: true,
    };

    constructor(private csarsProvider: CsarProvider, private platformsProvider: PlatformsProvider,
                private transformationProvider: TransformationsProvider, private route: ActivatedRoute,
                private routeHandler: RouteHandler) {
        this.routeHandler.setUp();
        this.title = this.selectPlatformString;
        this.platforms = this.platformsProvider.getPlatforms();
    }

    ngOnInit() {
        this.route.data.subscribe((data: { csar: Csar }) => {
            this.csar = data.csar;
            this.notAlreadyUsedPlatforms();
        });
    }


    public notAlreadyUsedPlatforms() {
        let platforms = [];
        if (!isNullOrUndefined(this.csar.transformations)) {
            const alreadyUsedPlatforms = this.csar.transformations.map(item => item.platform);
            platforms = this.platforms.filter(item => alreadyUsedPlatforms.lastIndexOf(item.id) !== -1);
        }
        this.alreadyUsedPlatforms = platforms;
    }

    async onPlatformSelected(id: string) {
        const res = this.alreadyUsedPlatforms.find(platform => platform.id === id);
        this.platform = id;
        if (!isNullOrUndefined(res)) {
            this.template.show();
        } else {
            await this.createNewTransformation(id);
        }
    }

    async overwriteTransformation() {
        await this.deleteTransformation();
        this.createNewTransformation(this.platform);
        this.template.hide();
        this.routeHandler.close();
    }

    deleteTransformation() {
        return this.transformationProvider.deleteTransformation(this.csar.name, this.platform);
    }

    private async createNewTransformation(id: string) {
        await this.transformationProvider.createNewTransformation(this.csar.name, id);
        this.toogle();
    }

    toogle() {
        this.platformSelected = !this.platformSelected;
        if (this.platformSelected) {
            this.title = this.enterInputsString;
        } else {
            this.title = this.selectPlatformString;
        }
    }

    onSubmit() {
        this.transformationProvider.startTransformation(this.csar.name, this.platform).then(() => {
            this.csar.addTransformation(this.platform, this.platformsProvider.getFullPlatformName(this.platform));
            this.csarsProvider.updateCsar(this.csar);
            this.routeHandler.openTransformation(this.csar.name, this.platform);
            // this close
        });

    }

    onExit() {
        if (this.platformSelected) {
            this.deleteTransformation();
        }
        // todo close
    }
}
