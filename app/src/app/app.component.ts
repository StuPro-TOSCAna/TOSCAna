import {Component, OnInit} from '@angular/core';
import {RouteHandler} from './handler/route/route.service';
import {Csar} from './model/csar';
import {PlatformsProvider} from './providers/platforms/platforms.provider';
import {CsarProvider} from './providers/csar/csar.provider';
import {HealthService} from './providers/health/health.service';
import {IntervalObservable} from 'rxjs/observable/IntervalObservable';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    status = '';
    memory = '';
    csars: Csar[] = [];
    listNotEmpty = false;

    constructor(private healthProvider: HealthService, private platformsProvider: PlatformsProvider,
                private csarProvider: CsarProvider, private routeHandler: RouteHandler) {
    }

    loadHealthStats() {
        IntervalObservable.create(2000).subscribe(() => {
            this.healthProvider.getHealthStatus().subscribe(data => {
                if (data.transformer.running_transformations.length === 0) {
                    this.status = 'IDLING';
                } else {
                    this.status = 'TRANSFORMING';
                }
                this.memory = this.convertToGb(data.diskSpace.total - data.diskSpace.free) + '/' + this.convertToGb(data.diskSpace.total) +
                    ' GB';
            });
        });
    }

    convertToGb(bytes: number) {
        const i = Math.floor(Math.log(bytes) / Math.log(1024));
        return (bytes / Math.pow(1024, Math.floor(i))).toFixed(1);
    }

    async ngOnInit() {
        await this.platformsProvider.loadPlatforms();
        await this.csarProvider.loadCsars();
        await this.csarProvider.csars.subscribe(async csars => {
            const viewState = await this.routeHandler.viewState.take(1).toPromise();
            this.csars = <Csar[]> csars;
            this.listNotEmpty = csars.length > 0;
            if (viewState === null && this.listNotEmpty) {
                this.routeHandler.openCsar(this.csars[0].name);
            } else if (viewState === null) {
                this.routeHandler.close();
            }
        });
        this.loadHealthStats();
    }

}
