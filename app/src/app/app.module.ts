import {RoutingModule} from './modules/routing/routing.module';
import {TransformationViewComponent} from './components/transformation-view/transformation-view.component';
import {ClientCsarsService} from './services/csar.service';
import {Configuration} from './api/configuration';
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {ModalModule, TooltipModule} from 'ngx-bootstrap';

import {AppComponent} from './app.component';
import {ApiModule} from './api/api.module';
import {CsarSideBarComponent} from './components/csar-sidebar/main-view.component';
import {CsarItemComponent} from './components/csar-item/csar-item.component';
import {CsarMenuSubItemsComponent} from './components/csar-sub-item/csar-menu-sub-items.component';
import {AngularFilePickerModule} from 'angular-file-picker-fixed';
import {FormsModule} from '@angular/forms';
import {TransformationCreatorComponent} from './components/transformation-creator/new-transformation-modal.component';
import {ClientPlatformsService} from './services/platforms.service';
import {TransformationInputsComponent} from './components/transformations-inputs/transformations-inputs.component';
import {ClientsTransformationsService} from './services/transformations.service';
import {LogComponent} from './components/log/log.component';
import {RouteHandler} from './services/route.service';
import {CsarViewComponent} from './components/csar-view/csar-view.component';
import {MessageComponent} from './components/message/message.component';
import {MessageService} from './services/message.service';
import {environment} from '../environments/environment';
import {HealthService} from './services/health.service';

@NgModule({
    entryComponents: [],
    declarations: [AppComponent, CsarSideBarComponent,
        CsarItemComponent, CsarMenuSubItemsComponent, TransformationViewComponent, TransformationCreatorComponent, TransformationInputsComponent,
        LogComponent, CsarViewComponent, MessageComponent],
    imports: [BrowserModule, ApiModule.forRoot(
        apiConfig), RoutingModule, AngularFilePickerModule, FormsModule, ModalModule.forRoot(), TooltipModule.forRoot()],
    providers: [ClientCsarsService, ClientPlatformsService, ClientsTransformationsService, RouteHandler, MessageService, HealthService],
    bootstrap: [AppComponent]
})
export class AppModule {
}

export function apiConfig() {
    return new Configuration({
        basePath: environment.apiUrl
    });
}
