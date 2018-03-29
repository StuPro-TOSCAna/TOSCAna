import {Component, OnInit, ViewChild} from '@angular/core';
import {ClientPlatformsService} from '../../services/platforms.service';
import {PlatformResponse} from '../../api';
import {ClientsTransformationsService} from '../../services/transformations.service';
import {Csar} from '../../model/csar';
import {ActivatedRoute} from '@angular/router';
import {ClientCsarsService} from '../../services/csar.service';
import {isNullOrUndefined} from 'util';
import {RouteHandler} from '../../services/route.service';
import 'rxjs/add/observable/fromPromise';

@Component({
    selector: 'app-transformation-creator',
    templateUrl: './transformation-creator.component.html',
    styleUrls: ['./transformation-creator.component.scss']
})
export class TransformationCreatorComponent implements OnInit {
    @ViewChild('template')
    template;
    csar: Csar;
    platforms: PlatformResponse[];
    alreadyUsedPlatforms: PlatformResponse[];
    platform: string;
    platformSelected = false;
    isNullOrUndefined = isNullOrUndefined;

    constructor(private csarsProvider: ClientCsarsService, private platformsProvider: ClientPlatformsService,
                private transformationProvider: ClientsTransformationsService, private route: ActivatedRoute,
                private routeHandler: RouteHandler) {
    }

    ngOnInit() {
        this.route.data.subscribe((data: { csar: Csar }) => {
            this.platforms = this.platformsProvider.getPlatforms();
            this.csar = data.csar;
            this.alreadyUsedPlatforms = this.collectAlreadyUsedPlatforms();
        });
    }

    /**
     * collects all transformations were already created for the current csar
     */
    public collectAlreadyUsedPlatforms(): PlatformResponse[] {
        let platforms = [];
        if (!isNullOrUndefined(this.csar.transformations)) {
            const alreadyUsedPlatforms = this.csar.transformations.map(item => item.platform);
            platforms = this.platforms.filter(item => alreadyUsedPlatforms.lastIndexOf(item.id) !== -1);
        }
        return platforms;
    }

    /**
     * called if transformation selected
     * case 1: if transformation already exists ask user if he wants to overwrite it
     * case 1: transformation does not exist => create new one
     * @param {string} selectedPlatformId
     * @returns {Promise<void>}
     */
    async onPlatformSelected(selectedPlatformId: string) {
        const res = this.alreadyUsedPlatforms.find(platform => platform.id === selectedPlatformId);
        this.platform = selectedPlatformId;
        if (!isNullOrUndefined(res)) {
            this.template.show();
        } else {
            await this.createNewTransformation(selectedPlatformId);
        }
    }

    /**
     * called if the user wants to overwrite the transformation
     * @returns {Promise<void>}
     */
    async overwriteTransformation() {
        await this.deleteTransformation().toPromise();
        this.createNewTransformation(this.platform);
        this.template.hide();
    }

    deleteTransformation() {
        return this.transformationProvider.deleteTransformation(this.csar.name, this.platform);
    }

    private async createNewTransformation(id: string) {
        await this.transformationProvider.createNewTransformation(this.csar.name, id).toPromise();
        this.openTransformationInputView();
    }

    openTransformationInputView() {
        this.routeHandler.openInputs(this.csar.name, this.platform);
        this.csarsProvider.addEmptyTransformationToCsar(this.csar.name, this.platform);
    }
}
