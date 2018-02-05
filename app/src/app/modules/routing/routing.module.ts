import {TransformationViewComponent} from '../../components/transformation-view/transformation-view.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NewTransformationModalComponent} from '../../components/new-transformation-modal/new-transformation-modal.component';
import {CanActivateNew} from './CsarResolver';
import {InputComponent} from '../../components/input/input.component';
import {CsarViewComponent} from '../../components/csar-view/csar-view.component';
import {HashLocationStrategy, LocationStrategy} from '@angular/common';

const routes: Routes = [
    {path: 'transformation/:csar/:platform', component: TransformationViewComponent}, {
        path: 'inputs/:csar/:platform',
        component: InputComponent
    }
    , {
        path: 'csar/:csar',
        component: CsarViewComponent
    },
    {
        path: 'new/:csarId',
        component: NewTransformationModalComponent,
        resolve: {
            csar: CanActivateNew
        }
    }
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes)
    ],
    providers: [CanActivateNew, {provide: LocationStrategy, useClass: HashLocationStrategy}],
    exports: [
        RouterModule
    ]
})
export class RoutingModule {
}
