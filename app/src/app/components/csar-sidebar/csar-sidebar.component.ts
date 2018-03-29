import {Csar} from './../../model/csar';
import {ClientCsarsService} from '../../services/csar.service';
import {Component, forwardRef, Input, OnInit, ViewChild} from '@angular/core';
import {PickedFile} from 'angular-file-picker-fixed/picked-file';
import {FilePickerDirective} from 'angular-file-picker-fixed';
import {isNullOrUndefined} from 'util';

const b64toBlob = require('b64-to-blob');

@Component({
    selector: 'app-csar-sidebar',
    templateUrl: './csar-sidebar.component.html',
    styleUrls: ['./csar-sidebar.component.scss']
})
export class CsarSideBarComponent implements OnInit {
    @Input() csars;

    @ViewChild(forwardRef(() =>
        FilePickerDirective)
    )
    private filePicker;
    public picked: PickedFile;
    uploadCsarActive = false;
    validFile = false;
    csarNameInput = '';
    csarInputMessage = '';

    constructor(public csarProvider: ClientCsarsService) {
    }

    ngOnInit() {
    }


    /**
     * toogles the csar uploadCsarActive view visibility
     */
    toogle() {
        this.uploadCsarActive = !this.uploadCsarActive;
        this.csarNameInput = '';
        this.validFile = false;
        this.picked = null;
    }

    /**
     * gets called when file is picked
     *
     * @param {PickedFile} file
     */
    onFilePicked(file: PickedFile) {
        this.picked = file;
        this.validFile = true;
        // if no name was entered prefill input with uploaded csar name
        if (this.csarNameInput === '') {
            let name: string[] = file.name.split('.');
            name = name.slice(0, name.length - 1);
            this.csarNameInput = name.join('.');
        }

    }

    /**
     * returns the icon class
     * class depends on if the csar name is valid
     * @param {boolean} nameValid
     * @returns {string} icon class
     */
    getIconClass(nameValid: boolean) {
        let res = 'fa';
        let valid = true;
        if (!nameValid) {
            this.csarInputMessage = 'Csar name is empty';
            valid = false;
        } else if (this.checkIfduplicatedCsarNameExists(this.csarNameInput)) {
            this.csarInputMessage = 'A csar with this name already exists.';
            valid = false;
        }
        if (!valid) {
            res += ' fa-exclamation-circle text-danger';
        } else {
            this.csarInputMessage = 'Everything is ok.';
            res += ' fa-check text-success';
        }
        return res;
    }

    submit() {
        if (this.csarNameInput === '') {
            return;
        }
        const csar = new Csar(this.csarNameInput, [], null);

        this.uploadCsar(csar);
        this.csarNameInput = '';
        this.picked = null;
        this.toogle();
    }

    private uploadCsar(csar) {
        const data = this.picked.content.split(',')[1];
        // noinspection TypeScriptValidateTypes
        const blob = b64toBlob(data, 'application/octet-stream');
        this.csarProvider.uploadCsar(csar, blob);
    }

    checkIfduplicatedCsarNameExists(csarId: string) {
        const res = this.csars.find(csar => csar.name === csarId);
        return (!isNullOrUndefined(res));
    }

    isInputFileAndTextValid(validInput: boolean) {
        return !validInput || !this.validFile || this.checkIfduplicatedCsarNameExists(this.csarNameInput);
    }


}
