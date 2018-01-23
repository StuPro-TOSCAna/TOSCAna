import {Injectable} from '@angular/core';
import {PropertyWrap, SetPropertiesRequest, TransformationsService} from '../../api';
import {PlatformsProvider} from '../platforms/platforms.provider';
import {Transformation} from '../../model/transformation';


@Injectable()
export class TransformationsProvider {

    constructor(private transformationsService: TransformationsService, private platformsProvider: PlatformsProvider) {
    }

    public async getTransformationProperties(csarId: string, platform: string) {
        let result = await this.transformationsService.getTransformationPropertiesUsingGET(csarId, platform).toPromise();
        return result.properties;
    }

    public async createNewTransformation(csarId: string, platform: string) {
        let result = await  this.transformationsService.addTransformationUsingPOST(csarId, platform).toPromise();
    }

    public setTransformationProperties(csarId: string, platform: string, properties: PropertyWrap[]) {
        const send: SetPropertiesRequest = {
            properties: properties
        };

        return this.transformationsService.setTransformationPropertiesUsingPUT(csarId, platform, send).toPromise();
    }

    public async getTransformationByCsarAndPlatform(csarId: string, platform: string): Promise<Transformation> {
        let result = await this.transformationsService.getCSARTransformationUsingGET(csarId, platform).toPromise();
        let fullName = this.platformsProvider.getFullPlatformName(result.platform);
        let transformation: Transformation = <Transformation>result;
        transformation.fullName = fullName;
        return Promise.resolve(transformation);
    }

    startTransformation(csarId: string, platform: string) {
        return this.transformationsService.startTransformationUsingPOST(csarId, platform).toPromise();
    }

    deleteTransformation(csarId: string, platform: string) {
        return this.transformationsService.deleteTransformationUsingDELETE(csarId, platform).toPromise();
    }

    getLogs(csarId: string, platform: string, num: number) {
        return this.transformationsService.getTransformationLogsUsingGET(csarId, platform, num);
    }

    getArtifactDownloadLink(csarId: string, platform: string) {
        return this.transformationsService.getTransformationArtifactUsingGET(csarId, platform);
    }
}
