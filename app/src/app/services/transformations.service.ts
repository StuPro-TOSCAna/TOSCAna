import {Injectable} from '@angular/core';
import {GetInputsResponse, GetOutputsResponse, InputsResponse, InputWrap, ResponseEntity, TransformationsService} from '../api/index';
import {ClientPlatformsService} from './platforms.service';
import {Transformation} from '../model/transformation';
import {Observable} from 'rxjs/Observable';
import {MessageService} from './message.service';


@Injectable()
export class ClientsTransformationsService {

    constructor(private messageService: MessageService, private transformationsService: TransformationsService,
                private platformsProvider: ClientPlatformsService) {
    }

    public getTransformationInputs(csarId: string, platform: string): Observable<GetInputsResponse> {
        return this.transformationsService.getInputsUsingGET(csarId, platform);
    }

    public getTransformationOutputs(csarId: string, platform: string): Observable<GetOutputsResponse> {
        return this.transformationsService.getOutputsUsingGET(csarId, platform);
    }

    public createNewTransformation(csarId: string, platform: string): Observable<any> {
        const observable = this.transformationsService.addTransformationUsingPOST(csarId, platform);
        observable.subscribe(() => {
        }, err => {
            this.messageService.addErrorMessage('Failed to create transformation');
        });
        return observable;
    }

    public setTransformationProperties(csarId: string, platform: string, inputs: InputWrap[]): Promise<InputsResponse> {
        const send: InputsResponse = {
            inputs: inputs
        };

        return this.transformationsService.setInputsUsingPUT(csarId, platform, send).toPromise();
    }

    public getTransformationByCsarAndPlatform(csarId: string, platform: string): Observable<Transformation> {
        return this.transformationsService.getCSARTransformationUsingGET(csarId, platform).map(transformation => {
            const fullName = this.platformsProvider.getFullPlatformName(transformation.platform);
            return new Transformation(fullName, transformation.phases, transformation.platform, transformation.state);
        });
    }

    public startTransformation(csarId: string, platform: string): Promise<ResponseEntity> {
        return this.transformationsService.startTransformationUsingPOST(csarId, platform).toPromise();
    }

    public deleteTransformation(csarId: string, platform: string): Observable<any> {
        return this.transformationsService.deleteTransformationUsingDELETE(csarId, platform);
    }

    public getLogs(csarId: string, platform: string, last: number): Observable<any> {
        return this.transformationsService.getTransformationLogsUsingGET(csarId, platform, last);
    }

}
