import {TransformationViewComponent} from '../../components/transformation-view/transformation-view.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NewTransformationModalComponent} from '../../components/new-transformation-modal/new-transformation-modal.component';
import {CanActivateNew} from './CsarResolver';

const routes: Routes = [
    {path: 'transformation/:csar/:platform', component: TransformationViewComponent}, {
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
    providers: [CanActivateNew],
    exports: [
        RouterModule
    ]
})
export class RoutingModule {
}
