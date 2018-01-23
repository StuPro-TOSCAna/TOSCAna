import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import 'rxjs/add/operator/switchMap';
import {PlatformsProvider} from '../../providers/platforms/platforms.provider';
import {TransformationsProvider} from '../../providers/transformations/transformations.provider';
import {Transformation} from '../../model/transformation';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/fromPromise';
import {IntervalObservable} from 'rxjs/observable/IntervalObservable';
import 'rxjs/add/operator/takeWhile';
import {RouteHandler} from '../../handler/route/route.service';
import {TransformationResponse} from '../../api';
import StateEnum = TransformationResponse.StateEnum;

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
    url = 'http://localhost:8084/api/csars/test/transformations/kubernetes/artifact';

    constructor(private routeHandler: RouteHandler, private route: ActivatedRoute,
                private transformationProvider: TransformationsProvider, public platformsProvider: PlatformsProvider) {
    }

    async ngOnInit() {
        this.routeHandler.setUp();
        this.route.paramMap.switchMap((params: ParamMap) => {
            this.csarId = params.get('csar');
            this.platform = params.get('platform');
            let promise = this.transformationProvider.getTransformationByCsarAndPlatform(this.csarId, this.platform);
            console.log(promise);
            return Observable.fromPromise(promise);
        }).subscribe(data => {
            this.transformationDone = false;
            this.transformation = data;
            IntervalObservable.create(1000).takeWhile(() => !this.transformationDone).subscribe(() => {
                Observable.fromPromise(this.transformationProvider.getTransformationByCsarAndPlatform(this.csarId, this.platform))
                    .subscribe(res => {
                        this.transformation = res;
                        if (res.state === StateEnum.DONE  || res.state === StateEnum.ERROR || res.state === StateEnum.INPUTREQUIRED) {
                            this.transformationDone = true;
                        }
                        this.logView.refresh();
                    });
            });
        });
    }
}
