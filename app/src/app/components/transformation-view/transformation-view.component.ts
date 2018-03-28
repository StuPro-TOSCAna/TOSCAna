import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import 'rxjs/add/operator/switchMap';
import {ClientPlatformsService} from '../../services/platforms.service';
import {ClientsTransformationsService} from '../../services/transformations.service';
import {Transformation} from '../../model/transformation';
import 'rxjs/add/observable/fromPromise';
import 'rxjs/add/operator/takeWhile';
import {RouteHandler} from '../../services/route.service';
import {InputWrap, LifecyclePhase, OutputWrap, TransformationResponse} from '../../api';
import {environment} from '../../../environments/environment';
import {isNullOrUndefined} from 'util';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/interval';
import {MessageService} from '../../services/message.service';
import {ClientCsarsService} from '../../services/csar.service';
import {getColorForLifecyclePhase} from '../../helper/helper';
import TransformationStateEnum = TransformationResponse.StateEnum;
import  LifecycleStateEnum = LifecyclePhase.StateEnum;

@Component({
    selector: 'app-transformation-view',
    templateUrl: './transformation-view.component.html',
    styleUrls: ['./transformation-view.component.scss']
})
export class TransformationViewComponent implements OnInit, OnDestroy {
    observable: Observable<number>;
    @ViewChild('Log') logView;
    transformation: Transformation;
    status: string;
    csarId: string;
    platform: string;
    transformationDone = false;
    url = '';
    state = 'log';
    private inputs: InputWrap[];
    private outputs: Array<OutputWrap> = [];
    getColorForLifecyclePhase = getColorForLifecyclePhase;

    constructor(private csarProvider: ClientCsarsService,
                private messageService: MessageService, private routeHandler: RouteHandler, private route: ActivatedRoute,
                private transformationProvider: ClientsTransformationsService, public platformsProvider: ClientPlatformsService) {
    }

    generateDownloadUrl() {
        this.url = `${environment.apiUrl}/api/csars/${this.csarId}/transformations/${this.platform}/artifact`;
    }

    async ngOnInit() {
        this.route.paramMap.switchMap((params: ParamMap) => {
            this.csarId = params.get('csar');
            this.platform = params.get('platform');
            return this.transformationProvider.getTransformationByCsarAndPlatform(this.csarId, this.platform);
        }).subscribe(data => {
            this.transformation = data;
            this.loadTransformationInputs();
            this.generateDownloadUrl();
            this.handleInputRequiredState();
            this.checkTransformationstate();
            this.pollForTransformationProgress();
        }, err => this.messageService.addErrorMessage('Failed to load transformation'));
    }

    /**
     * check the transformation state,
     * if it is INPUT_REQUIRED open the transformation input view
     */
    private handleInputRequiredState() {
        if (this.transformation.state === TransformationStateEnum.INPUTREQUIRED) {
            this.routeHandler.openInputs(this.csarId, this.platform);
        }
    }

    private loadTransformationInputs() {
        this.transformationProvider.getTransformationInputs(this.csarId, this.platform).subscribe(inputs => {
            this.inputs = inputs.inputs;
        });
    }

    private pollForTransformationProgress() {
        this.observable = Observable.interval(1000);
        this.observable.takeWhile(() => !this.transformationDone).subscribe(() => {
            this.transformationProvider.getTransformationByCsarAndPlatform(this.csarId, this.platform)
                .subscribe(res => {
                    this.transformation = res;
                    this.checkTransformationstate();
                    this.logView.refresh();
                });
        });
    }

    private checkTransformationstate() {
        if (isNullOrUndefined(this.transformation.state)) {
            return;
        }
        this.csarProvider.updateTransformationState(this.csarId, this.transformation.platform, this.transformation.state);
        if (this.transformation.state === TransformationStateEnum.DONE || this.transformation.state === TransformationStateEnum.ERROR ||
            this.transformation.state === TransformationStateEnum.INPUTREQUIRED) {
            this.transformationDone = true;
        }
    }

    getValue(value: string) {
        if (!isNullOrUndefined(value)) {
            if (value === '') {
                return '-';
            }
            return value;
        }
        return '-';
    }

    openOutputTab() {
        this.state = 'outputs';
        this.transformationProvider.getTransformationOutputs(this.csarId, this.platform).subscribe(data => {
            this.outputs = data.outputs;
        });
    }

    ngOnDestroy() {
        if (!isNullOrUndefined(this.observable)) {
            this.transformationDone = true;
        }
    }
}
