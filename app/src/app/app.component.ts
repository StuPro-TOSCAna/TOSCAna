import { PlatformsService } from './api/api/platforms.service';
import { Component } from '@angular/core';
import { log } from 'util';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
    title = 'app';
    test = '';
    constructor() {
    }
}
