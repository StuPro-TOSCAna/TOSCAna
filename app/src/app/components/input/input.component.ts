import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PropertyWrap} from '../../api';
import {TransformationsProvider} from '../../providers/transformations/transformations.provider';

@Component({
    selector: 'app-input',
    templateUrl: './input.component.html',
    styleUrls: ['./input.component.scss']
})
export class InputComponent implements OnInit {
    @Input() selectedPlatform: string;
    @Input() csarId: string;
    @Output() onSubmit = new EventEmitter();
    @Output() onExit = new EventEmitter();
    properties: PropertyWrap[] = [];
    errorMsg = false;

    constructor(private transformationsProvider: TransformationsProvider) {

    }

    async submit() {
        await this.transformationsProvider.setTransformationProperties(this.csarId, this.selectedPlatform, this.properties).then(result => {
            this.onSubmit.emit();
        }, err => {
            this.errorMsg = true;
        });
    }

    async ngOnInit() {
        this.properties = await this.transformationsProvider.getTransformationProperties(this.csarId, this.selectedPlatform);
    }

    exit() {
        this.onExit.emit();
    }
}
