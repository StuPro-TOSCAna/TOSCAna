import {RoutingModule} from './modules/routing/routing.module';
import {TransformationViewComponent} from './components/transformation-view/transformation-view.component';
import {CsarProvider} from './providers/csar/csar.provider';
import {Configuration} from './api/configuration';
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {ModalModule, TooltipModule} from 'ngx-bootstrap';

import {AppComponent} from './app.component';
import {ApiModule} from './api/api.module';
import {MainViewComponent} from './components/main-view/main-view.component';
import {CsarItemComponent} from './components/csar-item/csar-item.component';
import {CsarSubItemComponent} from './components/csar-sub-item/csar-sub-item.component';
import {AngularFilePickerModule} from 'angular-file-picker-fixed';
import {FormsModule} from '@angular/forms';
import {NewTransformationModalComponent} from './components/new-transformation-modal/new-transformation-modal.component';
import {PlatformsProvider} from './providers/platforms/platforms.provider';
import {InputComponent} from './components/input/input.component';
import {TransformationsProvider} from './providers/transformations/transformations.provider';
import {LogComponent} from './components/log/log.component';
import {RouteHandler} from './handler/route/route.service';
import {CsarViewComponent} from './components/csar-view/csar-view.component';
import { MessageComponent } from './components/message/message.component';
import {MessageService} from './providers/message/message.service';
import {environment} from '../environments/environment';

@NgModule({
    entryComponents: [],
    declarations: [AppComponent, MainViewComponent,
        CsarItemComponent, CsarSubItemComponent, TransformationViewComponent, NewTransformationModalComponent, InputComponent, LogComponent, CsarViewComponent, MessageComponent],
    imports: [BrowserModule, ApiModule.forRoot(apiConfig), RoutingModule, AngularFilePickerModule, FormsModule, ModalModule.forRoot(), TooltipModule.forRoot()],
    providers: [CsarProvider, PlatformsProvider, TransformationsProvider, RouteHandler, MessageService],
    bootstrap: [AppComponent]
})
export class AppModule {
}

export function apiConfig() {
    return new Configuration({
        basePath: environment.apiUrl
    });
}
