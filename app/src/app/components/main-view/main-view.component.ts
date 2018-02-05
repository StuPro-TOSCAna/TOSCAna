import {Csar} from './../../model/csar';
import {CsarProvider} from '../../providers/csar/csar.provider';
import {Component, forwardRef, Input, OnInit, ViewChild} from '@angular/core';
import {PickedFile} from 'angular-file-picker-fixed/picked-file';
import {FilePickerDirective} from 'angular-file-picker-fixed';

const b64toBlob = require('b64-to-blob');

@Component({
    selector: 'app-main-view',
    templateUrl: './main-view.component.html',
    styleUrls: ['./main-view.component.scss']
})
export class MainViewComponent implements OnInit {
    @Input() csars;

    @ViewChild(forwardRef(() =>
        FilePickerDirective)
    )
    private filePicker;
    public picked: PickedFile;
    upload = false;
    validFile = false;
    input = '';

    constructor(public csarProvider: CsarProvider) {
    }

    async ngOnInit() {
    }


    toogle() {
        this.upload = !this.upload;
        this.input = '';
        this.validFile = false;
        this.picked = null;
    }

    onFilePicked(file: PickedFile) {
        this.picked = file;
        this.validFile = true;
        if (this.input === '') {
            let name: string[] = file.name.split('.');
            name = name.slice(0, name.length - 1);
            this.input = name.join('.');
        }

    }

    submit() {
        if (this.input === '') {
            return;
        }
        const csar = new Csar(this.input, [], null);

        const data = this.picked.content.split(',')[1];
        // noinspection TypeScriptValidateTypes
        const blob = b64toBlob(data, 'application/octet-stream');
        this.csarProvider.uploadCsar(csar, blob);
        this.input = '';
        this.picked = null;
        this.toogle();
    }

    disableButton(validInput: boolean) {
        return !validInput || !this.validFile;
    }


}
