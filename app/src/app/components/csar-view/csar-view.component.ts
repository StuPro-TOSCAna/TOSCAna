import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import 'rxjs/add/operator/switchMap';
import {PlatformsProvider} from '../../providers/platforms/platforms.provider';
import 'rxjs/add/observable/fromPromise';
import 'rxjs/add/operator/takeWhile';
import {RouteHandler} from '../../handler/route/route.service';
import {CsarProvider} from '../../providers/csar/csar.provider';
import {Csar} from '../../model/csar';
import {LifecyclePhase} from '../../api';
import {isNullOrUndefined} from 'util';
import StateEnum = LifecyclePhase.StateEnum;

@Component({
    selector: 'app-csar-view',
    templateUrl: './csar-view.component.html',
    styleUrls: ['./csar-view.component.scss']
})
export class CsarViewComponent implements OnInit {
    @ViewChild('Log') logView;
    csar: Csar;
    status: string;
    csarId: string;
    platform: string;
    transformationDone = false;
    url = '';

    constructor(private csarProvider: CsarProvider, private routeHandler: RouteHandler, private route: ActivatedRoute,
                public platformsProvider: PlatformsProvider) {
    }

    isActive(phase: LifecyclePhase) {
        if (phase.state === StateEnum.EXECUTING) {
            return true;
        }
        return false;
    }

    getIcon(phase: LifecyclePhase) {
        let res = '';
        if (phase.state === StateEnum.DONE) {
            res = 'text-success';
        } else if (phase.state === StateEnum.FAILED) {
            res = 'text-danger';
        } else if (phase.state === StateEnum.EXECUTING) {
            res = 'text-orange';
        } else if (phase.state === StateEnum.PENDING) {
            res = 'text-primary';
        } else if (phase.state === StateEnum.SKIPPING) {
            res = 'text-secondary';
        }
        return res + ' state';
    }


    async ngOnInit() {
        this.route.paramMap.switchMap((params: ParamMap) => {
            this.csarId = params.get('csar');
            return this.csarProvider.getCsarByName(this.csarId);
        }).subscribe(data => {
            this.transformationDone = false;
            if (isNullOrUndefined(data)) {
                this.csar = null;
            }
            data.then(csar => {
                this.csar = csar;
                this.routeHandler.setUpCsar(this.csarId);
            });
            // IntervalObservable.create(1000).takeWhile(() => !this.transformationDone).subscribe(() => {
            //     this.csarProvider.getCsarByName(this.csarId)
            //         .subscribe(res => {
            //             this.csar = res;
            //             this.checkTransformationstate();
            //             //this.logView.refresh();
            //         });
            // });
        });
    }

    private checkTransformationstate() {
        // if (this.csar. === TransformationStateEnum.DONE || this.transformation.state === TransformationStateEnum.ERROR || this.transformation.state === TransformationStateEnum.INPUTREQUIRED) {
        //   this.transformationDone = true;
        //}
    }
}
