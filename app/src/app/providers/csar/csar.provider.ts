import {Csar} from './../../model/csar';
import {CsarsService} from './../../api/api/csars.service';
import {Injectable} from '@angular/core';
import {PlatformsProvider} from '../platforms/platforms.provider';
import {log} from 'util';
import {Transformation} from '../../model/transformation';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Helper} from '../../helper/helper';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {CsarResponse} from '../../api';
import {Observable} from 'rxjs/Observable';

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

    constructor(public csarService: CsarsService, private platformsProvider: PlatformsProvider) {
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
                    this._csars.next(Object.assign({}, this.dataStore).csars);
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
            const csarObject = new Csar(csar.name, transformations);
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
            log(error);
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

    private updateSubject() {
        console.log('next');
        this._csars.next((Object.assign({}, this.dataStore).csars));
    }

    getTransformations(csarId: string) {
        const promise = this.csarService.getCSARTransformationsUsingGET(csarId);
        return promise.toPromise();
    }

    getCsarById(csarId: string): Csar {
        return this.dataStore.csars.find(csar => csar.name === csarId);
    }
}
