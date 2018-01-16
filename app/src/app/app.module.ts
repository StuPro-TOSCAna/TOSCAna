import { Configuration } from './api/configuration';
import { TransformationsService } from './api/api/transformations.service';
import { CsarsService } from './api/api/csars.service';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { ApiModule } from './api/api.module';

@NgModule({
    declarations: [AppComponent],
    imports: [BrowserModule, NgbModule.forRoot(), ApiModule.forRoot(apiConfig)],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {}

export function apiConfig() {
    return new Configuration({
        basePath: 'https://stupro-toscana.de'
    });
}
