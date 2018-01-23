import {Csar} from './../../model/csar';
import {CsarProvider} from '../../providers/csar/csar.provider';
import {ChangeDetectorRef, Component, forwardRef, Input, OnInit, ViewChild} from '@angular/core';
import {PickedFile} from 'angular-file-picker-fixed/picked-file';
import {FilePickerDirective} from 'angular-file-picker-fixed';
import {PlatformsProvider} from '../../providers/platforms/platforms.provider';
import {log} from 'util';

const b64toBlob = require('b64-to-blob');

@Component({
    selector: 'app-main-view',
    templateUrl: './main-view.component.html',
    styleUrls: ['./main-view.component.scss']
})
export class MainViewComponent implements OnInit {
    @Input() split;

    @ViewChild(forwardRef(() =>
        FilePickerDirective)
    )
    private filePicker;
    public picked: PickedFile;
    csars: Csar[] = [];
    upload = false;
    validFile = false;
    input = '';

    constructor(private changeDetection: ChangeDetectorRef, public csarProvider: CsarProvider,
                private platformsProvider: PlatformsProvider) {
    }

    async ngOnInit() {
        await this.platformsProvider.loadPlatforms();
        await this.csarProvider.loadCsars();
        await this.csarProvider.csars.subscribe(csars => {
            console.log(csars);
            this.csars = <Csar[]> csars;
        });
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
        const csar = new Csar(this.input, null);

        const data = this.picked.content.split(',')[1];
        // noinspection TypeScriptValidateTypes
        const blob = b64toBlob(data, 'application/octet-stream');
        this.csarProvider.uploadCsar(csar, blob);
        this.input = '';
        this.picked = null;
        this.toogle();
    }

    setFlexWrap() {
        if (this.split) {
            return {'flex-wrap': 'wrap'};
        } else {
            return '';
        }
    }

    disableButton(validInput: boolean) {
        return !validInput || !this.validFile;
    }


}
