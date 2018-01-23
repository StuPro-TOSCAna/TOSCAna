import {Component, OnInit} from '@angular/core';
import {RouteHandler} from './handler/route/route.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    mainView = 'col';
    splitPane = '';
    split = false;

    constructor(private routeHandler: RouteHandler) {
    }

    ngOnInit() {
        this.listenOnRouteChanges();
    }

    listenOnRouteChanges() {
        this.routeHandler.open.subscribe(open => {
            if (open) {
                this.mainView = 'col-sm-5 left';
                this.splitPane = 'col';
                this.split = true;
            } else {
                this.mainView = 'col';
                this.splitPane = '';
                this.split = false;
            }
        });
    }
}
