import {Injectable} from '@angular/core';
import {InputsResponse, InputWrap, TransformationsService} from '../../api';
import {PlatformsProvider} from '../platforms/platforms.provider';
import {Transformation} from '../../model/transformation';
import {Observable} from 'rxjs/Observable';
import {MessageService} from '../message/message.service';


@Injectable()
export class TransformationsProvider {

    constructor(private messageService: MessageService, private transformationsService: TransformationsService,
                private platformsProvider: PlatformsProvider) {
    }

    public getTransformationInputs(csarId: string, platform: string) {
        return this.transformationsService.getInputsUsingGET(csarId, platform);
    }

    public getTransformationOutputs(csarId: string, platform: string) {
        return this.transformationsService.getOutputsUsingGET(csarId, platform);
    }

    public createNewTransformation(csarId: string, platform: string) {
        const observable = this.transformationsService.addTransformationUsingPOST(csarId, platform);
        observable.subscribe(() => {
        }, err => {
            this.messageService.addErrorMessage('Failed to create transformation');
        });
        return observable;
    }

    public setTransformationProperties(csarId: string, platform: string, inputs: InputWrap[]) {
        const send: InputsResponse = {
            inputs: inputs
        };

        return this.transformationsService.setInputsUsingPUT(csarId, platform, send).toPromise();
    }

    public getTransformationByCsarAndPlatform(csarId: string, platform: string): Observable<Transformation> {
        return this.transformationsService.getCSARTransformationUsingGET(csarId, platform).map(result => {
            let fullName = this.platformsProvider.getFullPlatformName(result.platform);
            let transformation: Transformation = <Transformation>result;
            transformation.fullName = fullName;
            return transformation;
        });
    }

    startTransformation(csarId: string, platform: string) {
        return this.transformationsService.startTransformationUsingPOST(csarId, platform).toPromise();
    }

    deleteTransformation(csarId: string, platform: string) {
        return this.transformationsService.deleteTransformationUsingDELETE(csarId, platform);
    }

    getLogs(csarId: string, platform: string, num: number) {
        return this.transformationsService.getTransformationLogsUsingGET(csarId, platform, num);
    }

    getArtifactDownloadLink(csarId: string, platform: string) {
        return this.transformationsService.getTransformationArtifactUsingGET(csarId, platform);
    }
}
