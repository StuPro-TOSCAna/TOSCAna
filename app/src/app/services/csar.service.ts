import {Csar} from '../model/csar';
import {CsarsService} from '../api/api/csars.service';
import {Injectable} from '@angular/core';
import {ClientPlatformsService} from './platforms.service';
import {Transformation} from '../model/transformation';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {CsarResponse, TransformationResponse} from '../api/index';
import {Observable} from 'rxjs/Observable';
import {MessageService} from './message.service';
import {ErrorMessage} from '../model/message';
import {isNullOrUndefined} from 'util';
import StateEnum = TransformationResponse.StateEnum;

@Injectable()
export class ClientCsarsService {
    _csars: BehaviorSubject<Csar[]>;
    loading = false;
    res = null;
    private dataStore: {
        csars: Csar[]
    };
    private requested = false;

    constructor(private messageService: MessageService, public csarService: CsarsService, private platformsProvider: ClientPlatformsService) {
        this.dataStore = {csars: []};
        this._csars = <BehaviorSubject<Csar[]>>new BehaviorSubject([]);
    }

    deleteCsar(csarId: string) {
        this.csarService.deleteCsarUsingDELETE(csarId).subscribe(() => {
            const csar = this.dataStore.csars.find(item => item.name === csarId);
            this.dataStore.csars.splice(this.dataStore.csars.indexOf(csar), 1);
            this.updateSubject();
        }, err => {
            this.messageService.addErrorMessage('Failed to delete csar');
        });
    }

    public getCount() {
        return this.dataStore.csars.length;
    }

    loadCsars() {
        console.log('request');
        if (!this.requested) {
            this.requested = true;
            const observ = this.csarService.listCSARsUsingGET();
            this.res = observ.map(data => {
                if (isNullOrUndefined(data._embedded)) {
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
            }, err => this.messageService.addErrorMessage('Failed to load csars'));
        }
    }

    private async createCsarWithTransformations(csars: Array<CsarResponse>) {
        const res = [];
        for (const csar of csars) {
            const transformations = await this.getTransformations(csar.name).toPromise();
            console.log(transformations);
            const csarObject = new Csar(csar.name, csar.phases, transformations);
            res.push(csarObject);
        }
        return res;
    }

    get csars() {
        return this._csars.asObservable();
    }

    uploadCsar(csar: Csar, blob: Blob) {
        this.csarService.uploadCSARUsingPUT(csar.name, blob).subscribe(value => {
            this.dataStore.csars.push(csar);
            this.updateSubject();
        }, error => {
            this.messageService.add(new ErrorMessage('CSAR upload failed.'));
        });
    }

    updateCsar(csar: Csar) {
        this.dataStore.csars.forEach((c, i) => {
            if (c.name === csar.name) {
                this.dataStore.csars[i] = csar;
                this.updateSubject();
                return;
            }
        });
    }

    addEmptyTransformationToCsar(csarId, platform: string) {
        const res = this.dataStore.csars.find(csar => csar.name === csarId);
        const fullName = this.platformsProvider.getFullPlatformName(platform);
        res.addTransformation(platform, fullName);
        this.updateSubject();
    }

    updateTransformationState(csarId: string, platform: string, state: StateEnum) {
        const res = this.dataStore.csars.find(csar => csar.name === csarId);
        res.transformations.find(t => t.platform === platform).state = state;
        this.updateSubject();
    }

    private updateSubject() {
        this._csars.next((Object.assign({}, this.dataStore).csars));
    }

    getTransformations(csarId: string): Observable<Transformation[]> {
        return this.csarService.getCSARTransformationsUsingGET(csarId).map(result => {
            if (isNullOrUndefined(result._embedded)) {
                return [];
            }
            const res = result._embedded.transformation;
            const transformations = [];
            res.forEach(transformation => {
                const fullName = this.platformsProvider.getFullPlatformName(transformation.platform);
                transformations.push(new Transformation(fullName, transformation.phases, transformation.platform, transformation.state));
            });
            return transformations;
        });
    }


    getCsarByName(csarId: string) {
        return this.csarService.getCSARInfoUsingGET(csarId).map(async data => {
            const res = await this.createCsarWithTransformations([data]);
            return res[0];
        }, err => {
            this.messageService.addErrorMessage(`Failed to load the csar "${csarId}"`);
        });
    }

    getCsarById(csarId: string): Csar {
        return this.dataStore.csars.find(csar => csar.name === csarId);
    }

    getLogs(csarId: string, last: number) {
        return this.csarService.getLogsUsingGET(csarId, last);
    }
}
