import {Csar} from './../../model/csar';
import {CsarProvider} from '../../providers/csar/csar.provider';
import {Component, forwardRef, Input, OnInit, ViewChild} from '@angular/core';
import {PickedFile} from 'angular-file-picker-fixed/picked-file';
import {FilePickerDirective} from 'angular-file-picker-fixed';
import {isNullOrUndefined} from 'util';

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
    message = '';

    constructor(public csarProvider: CsarProvider) {
    }

    ngOnInit() {
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

    getIcon(nameValid: boolean) {
        let res = 'fa';
        let valid = true;
        if (!nameValid) {
            this.message = 'Csar name is empty';
            valid = false;
        } else if (this.duplicatedCsarNameExists(this.input)) {
            this.message = 'A csar with this name already exists.';
            valid = false;
        }
        if (!valid) {
            res += ' fa-exclamation-circle text-danger';
        } else {
            this.message = 'Everything is ok.';
            res += ' fa-check text-success';
        }
        return res;
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

    duplicatedCsarNameExists(csarId: string) {
        const res = this.csars.find(csar => csar.name === csarId);
        return (!isNullOrUndefined(res));
    }

    disableButton(validInput: boolean) {
        return !validInput || !this.validFile || this.duplicatedCsarNameExists(this.input);
    }


}
