import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import 'rxjs/add/operator/switchMap';
import {PlatformsProvider} from '../../providers/platforms/platforms.provider';
import {TransformationsProvider} from '../../providers/transformations/transformations.provider';
import {Transformation} from '../../model/transformation';
import 'rxjs/add/observable/fromPromise';
import {IntervalObservable} from 'rxjs/observable/IntervalObservable';
import 'rxjs/add/operator/takeWhile';
import {RouteHandler} from '../../handler/route/route.service';
import {LifecyclePhase, TransformationResponse} from '../../api';
import TransformationStateEnum = TransformationResponse.StateEnum;
import  LifecycleStateEnum = LifecyclePhase.StateEnum;
import {environment} from '../../../environments/environment';

@Component({
    selector: 'app-transformation-view',
    templateUrl: './transformation-view.component.html',
    styleUrls: ['./transformation-view.component.scss']
})
export class TransformationViewComponent implements OnInit {
    @ViewChild('Log') logView;
    transformation: Transformation;
    status: string;
    csarId: string;
    platform: string;
    transformationDone = false;
    url = '';

    constructor(private routeHandler: RouteHandler, private route: ActivatedRoute,
                private transformationProvider: TransformationsProvider, public platformsProvider: PlatformsProvider) {
    }

    isActive(phase: LifecyclePhase) {
        if (phase.state === LifecycleStateEnum.EXECUTING) {
            return true;
        }
        return false;
    }

    getIcon(phase: LifecyclePhase) {
        if (phase.state === LifecycleStateEnum.DONE) {
            return 'green';
        } else if (phase.state === LifecycleStateEnum.FAILED) {
            return 'red';
        } else if (phase.state === LifecycleStateEnum.EXECUTING) {
            return 'orange';
        } else if (phase.state === LifecycleStateEnum.PENDING) {
            return 'blue';
        } else if (phase.state === LifecycleStateEnum.SKIPPING) {
            return 'gray';
        }
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
            this.transformationDone = false;
            this.transformation = data;
            this.generateDownloadUrl();
            if (this.transformation.state === TransformationStateEnum.INPUTREQUIRED) {
                this.routeHandler.openInputs(this.csarId, this.platform);
            }
            this.checkTransformationstate();
            IntervalObservable.create(1000).takeWhile(() => !this.transformationDone).subscribe(() => {
                this.transformationProvider.getTransformationByCsarAndPlatform(this.csarId, this.platform)
                    .subscribe(res => {
                        this.transformation = res;
                        this.checkTransformationstate();
                        this.logView.refresh();
                    });
            });
        });
    }

    private checkTransformationstate() {
        if (this.transformation.state === TransformationStateEnum.DONE || this.transformation.state === TransformationStateEnum.ERROR || this.transformation.state === TransformationStateEnum.INPUTREQUIRED) {
            this.transformationDone = true;
        }
    }
}
