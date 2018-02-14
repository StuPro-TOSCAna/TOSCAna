import {Component, OnInit} from '@angular/core';
import {RouteHandler} from './handler/route/route.service';
import {Csar} from './model/csar';
import {PlatformsProvider} from './providers/platforms/platforms.provider';
import {CsarProvider} from './providers/csar/csar.provider';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

    csars: Csar[] = [];
    listNotEmpty = false;

    constructor(private platformsProvider: PlatformsProvider, private csarProvider: CsarProvider, private routeHandler: RouteHandler) {
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
    }

}
