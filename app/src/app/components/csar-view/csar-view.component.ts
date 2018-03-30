import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import 'rxjs/add/operator/switchMap';
import {ClientPlatformsService} from '../../services/platforms.service';
import 'rxjs/add/observable/fromPromise';
import 'rxjs/add/operator/takeWhile';
import {RouteHandler} from '../../services/route.service';
import {ClientCsarsService} from '../../services/csar.service';
import {Csar} from '../../model/csar';
import {LifecyclePhase} from '../../api';
import {isNullOrUndefined} from 'util';
import {Observable} from 'rxjs/Observable';
import StateEnum = LifecyclePhase.StateEnum;
import {getColorForLifecyclePhase} from '../../helper/helper';

@Component({
    selector: 'app-csar-view',
    templateUrl: './csar-view.component.html',
    styleUrls: ['./csar-view.component.scss']
})
export class CsarViewComponent implements OnInit, OnDestroy {
    observable: Observable<number>;
    @ViewChild('Log') logView;
    csar: Csar;
    status: string;
    csarId: string;
    platform: string;
    csarProcessingDone = false;
    downloadUrl = '';
    getColorForLifecyclePhase = getColorForLifecyclePhase;
    StateEnum = StateEnum;

    constructor(private csarProvider: ClientCsarsService, private routeHandler: RouteHandler, private route: ActivatedRoute,
                public platformsProvider: ClientPlatformsService) {
    }

    async ngOnInit() {
        this.route.paramMap.switchMap((params: ParamMap) => {
            this.csarId = params.get('csar');
            return this.csarProvider.getCsarByName(this.csarId);
        }).subscribe(data => {
            this.csarProcessingDone = false;
            if (isNullOrUndefined(data)) {
                this.csar = null;
            }
            data.then(csar => {
                this.csar = csar;
                this.routeHandler.setUpCsar(this.csarId);
            });
            this.pollForProcessingUpdates();
        });
    }

    /**
     * polls until the csar processing is done
     */
    private pollForProcessingUpdates() {
        this.observable = Observable.interval(1000);
        this.observable.takeWhile(() => !this.csarProcessingDone).subscribe(() => {
            this.csarProvider.getCsarByName(this.csarId)
                .subscribe(res => {
                    res.then(data => {
                        this.csar = data;
                    });
                    this.checkIfProcessingIsDone();
                    this.logView.loadNewLogEntries();
                });
        });
    }

    private checkIfProcessingIsDone() {
        this.csarProvider.updateCsar(this.csar);
        this.csarProcessingDone = !this.csar.currentlyParsing();
    }

    ngOnDestroy() {
        if (!isNullOrUndefined(this.observable)) {
            this.csarProcessingDone = true;
        }
    }
}
