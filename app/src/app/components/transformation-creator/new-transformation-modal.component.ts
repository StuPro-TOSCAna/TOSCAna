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
    title: string;
    platform: string;
    platformSelected = false;
    selectPlatformString = 'Select Platform';
    isNullOrUndefined = isNullOrUndefined;

    constructor(private csarsProvider: ClientCsarsService, private platformsProvider: ClientPlatformsService,
                private transformationProvider: ClientsTransformationsService, private route: ActivatedRoute,
                private routeHandler: RouteHandler) {
        this.title = this.selectPlatformString;
    }

    ngOnInit() {
        this.route.data.subscribe((data: { csar: Csar }) => {
            this.platforms = this.platformsProvider.getPlatforms();
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
        await this.deleteTransformation().toPromise();
        this.createNewTransformation(this.platform);
        this.template.hide();
    }

    deleteTransformation() {
        return this.transformationProvider.deleteTransformation(this.csar.name, this.platform);
    }

    private async createNewTransformation(id: string) {
        await this.transformationProvider.createNewTransformation(this.csar.name, id).toPromise();
        this.openInputs();
    }

    openInputs() {
        this.routeHandler.openInputs(this.csar.name, this.platform);
        this.csarsProvider.addEmptyTransformationToCsar(this.csar.name, this.platform);
    }
}
