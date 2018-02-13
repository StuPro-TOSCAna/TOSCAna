import {Csar} from './../../model/csar';
import {CsarsService} from './../../api/api/csars.service';
import {Injectable} from '@angular/core';
import {PlatformsProvider} from '../platforms/platforms.provider';
import {Transformation} from '../../model/transformation';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Helper} from '../../helper/helper';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {CsarResponse} from '../../api';
import {Observable} from 'rxjs/Observable';
import {MessageService} from '../message/message.service';
import {ErrorMessage} from '../../model/message';

@Injectable()
export class CsarProvider {
    _csars: BehaviorSubject<Csar[]>;
    notNullOrUndefined = Helper.notNullOrUndefined;
    loading = false;
    res = null;
    private dataStore: {
        csars: Csar[]
    };
    private requested = false;

    constructor(private messageService: MessageService, public csarService: CsarsService, private platformsProvider: PlatformsProvider) {
        this.dataStore = {csars: []};
        this._csars = <BehaviorSubject<Csar[]>>new BehaviorSubject([]);
    }

    deleteCsar(csarId: string) {
        this.csarService.deleteCsarUsingDELETE(csarId).subscribe(() => {
            let csar = this.dataStore.csars.find(item => item.name === csarId);
            this.dataStore.csars.splice(this.dataStore.csars.indexOf(csar), 1);
            this.updateSubject();
        });
    }

    public getCount() {
        return this.dataStore.csars.length;
    }

    loadCsars() {
        console.log('request');
        if (!this.requested) {
            this.requested = true;
            let observ = this.csarService.listCSARsUsingGET();
            this.res = observ.map(data => {
                if (!this.notNullOrUndefined(data._embedded)) {
                    return Observable.of();
                }
                const csars = data._embedded.csar;
                return this.createCsarWithTransformations(csars);
            });
            this.res.subscribe(data => {
                data.then(csarResult => {
                    this.dataStore.csars = csarResult;
                    this.updateSubject();
                    this.loading = true;
                });
            }, err => console.log(err));
        }
    }

    private async createCsarWithTransformations(csars: Array<CsarResponse>) {
        let res = [];
        for (const csar of csars) {
            const transformationsPromise = await this.getTransformations(csar.name);
            const transformations = [];
            if (transformationsPromise._embedded !== undefined) {
                this.addFullNameToTransformations(transformationsPromise, transformations);
            }
            const csarObject = new Csar(csar.name, csar.phases, transformations);
            res.push(csarObject);
        }
        return res;
    }

    private addFullNameToTransformations(transformationsPromise: any, transformations: any[]) {
        const transformationResponses = transformationsPromise._embedded.transformation;
        for (const item of transformationResponses) {
            const fullName: string = this.platformsProvider.getFullPlatformName(item.platform);
            const transformation = <Transformation> item;
            transformation.fullName = fullName;
            transformations.push(transformation);
        }
    }

    get csars() {
        return this._csars.asObservable();
    }

    uploadCsar(csar: Csar, blob: Blob) {
        this.csarService.uploadCSARUsingPUT(csar.name, blob).subscribe(value => {
            this.dataStore.csars.push(csar);
            this.updateSubject();
            console.log(csar);
        }, error => {
            this.messageService.add(new ErrorMessage('fail'));
        });
    }

    updateCsar(csar: Csar) {
        this.dataStore.csars.forEach((c, i) => {
            if (c.name === csar.name) {
                this.dataStore.csars[i] = csar;
            }
            this.updateSubject();
        });
    }

    addEmptyTransformationToCsar(csarId, platform: string) {
        const res = this.dataStore.csars.find(csar => csar.name = csarId);
        const fullName = this.platformsProvider.getFullPlatformName(platform);
        res.addTransformation(platform, fullName);
        this.updateSubject();
    }

    private updateSubject() {
        this._csars.next((Object.assign({}, this.dataStore).csars));
    }

    getTransformations(csarId: string) {
        const promise = this.csarService.getCSARTransformationsUsingGET(csarId);
        return promise.toPromise();
    }


    getCsarByName(csarId: string) {
        return this.csarService.getCSARInfoUsingGET(csarId).map(async data => {
            let res = await this.createCsarWithTransformations([data]);
            return res[0];
        });
    }

    getCsarById(csarId: string): Csar {
        return this.dataStore.csars.find(csar => csar.name === csarId);
    }

    getLogs(csarId: string, last: number) {
        return this.csarService.getLogsUsingGET(csarId, last);
    }
}
