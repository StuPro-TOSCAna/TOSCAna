import {TransformationViewComponent} from '../../components/transformation-view/transformation-view.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TransformationCreatorComponent} from '../../components/transformation-creator/new-transformation-modal.component';
import {CsarResolver} from './csar.resolver';
import {TransformationInputsComponent} from '../../components/transformations-inputs/transformations-inputs.component';
import {CsarViewComponent} from '../../components/csar-view/csar-view.component';
import {HashLocationStrategy, LocationStrategy} from '@angular/common';

const routes: Routes = [
    {path: 'transformation/:csar/:platform', component: TransformationViewComponent}, {
        path: 'inputs/:csar/:platform',
        component: TransformationInputsComponent
    }
    , {
        path: 'csar/:csar',
        component: CsarViewComponent
    },
    {
        path: 'new/:csarId',
        component: TransformationCreatorComponent,
        resolve: {
            csar: CsarResolver
        }
    }
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes)
    ],
    providers: [CsarResolver, {provide: LocationStrategy, useClass: HashLocationStrategy}],
    exports: [
        RouterModule
    ]
})
export class RoutingModule {
}
