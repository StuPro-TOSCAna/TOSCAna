import {Csar} from '../model/csar';
import {CsarsService} from '../api/api/csars.service';
import {Injectable} from '@angular/core';
import {ClientPlatformsService} from './platforms.service';
import {Transformation} from '../model/transformation';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {CsarResponse, LogResponse, TransformationResponse} from '../api/index';
import {Observable} from 'rxjs/Observable';
import {MessageService} from './message.service';
import {ErrorMessage} from '../model/message';
import {isNullOrUndefined} from 'util';
import StateEnum = TransformationResponse.StateEnum;

@Injectable()
export class ClientCsarsService {
    private _csars: BehaviorSubject<Csar[]>;
    private loading = false;
    private res = null;
    private dataStore: {
        csars: Csar[]
    };
    private requested = false;

    constructor(private messageService: MessageService, public csarService: CsarsService, private platformsProvider: ClientPlatformsService) {
        this.dataStore = {csars: []};
        this._csars = <BehaviorSubject<Csar[]>>new BehaviorSubject([]);
    }

    public deleteCsar(csarId: string) {
        this.csarService.deleteCsarUsingDELETE(csarId).subscribe(() => {
            const csar = this.dataStore.csars.find(item => item.name === csarId);
            this.dataStore.csars.splice(this.dataStore.csars.indexOf(csar), 1);
            this.updateSubject();
        }, err => {
            this.messageService.addErrorMessage('Failed to delete csar');
        });
    }

    public getCount():number {
        return this.dataStore.csars.length;
    }

    public loadCsars() {
        if (!this.requested) {
            this.requested = true;
            const observ = this.csarService.listCSARsUsingGET();
            this.res = observ.map(data => {
                if (isNullOrUndefined(data._embedded)) {
                    return Observable.of();
                }
                const csars = data._embedded.csar;
                return this.addTransformationsToCsars(csars);
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

    /**
     * loads all transformations for a csar and adds them
     * @param {Array<CsarResponse>} csars
     * @returns {Promise<any[]>}
     */
    private async addTransformationsToCsars(csars: Array<CsarResponse>) {
        const res = [];
        for (const csar of csars) {
            const transformations = await this.getTransformations(csar.name).toPromise();
            const csarObject = new Csar(csar.name, csar.phases, transformations);
            res.push(csarObject);
        }
        return res;
    }

    get csars(): Observable<Csar[]> {
        return this._csars.asObservable();
    }

    public uploadCsar(csar: Csar, blob: Blob) {
        this.csarService.uploadCSARUsingPUT(csar.name, blob).subscribe(value => {
            this.dataStore.csars.push(csar);
            this.updateSubject();
        }, error => {
            this.messageService.add(new ErrorMessage('CSAR upload failed.'));
        });
    }

    public updateCsar(csar: Csar) {
        this.dataStore.csars.forEach((c, i) => {
            if (c.name === csar.name) {
                this.dataStore.csars[i] = csar;
                this.updateSubject();
                return;
            }
        });
    }

    /**
     * Creates transformation with only a platform
     * this is needed to show menu entries
     * @param csarId
     * @param {string} platform
     */
    public addEmptyTransformationToCsar(csarId, platform: string) {
        const res = this.dataStore.csars.find(csar => csar.name === csarId);
        const fullName = this.platformsProvider.getFullPlatformName(platform);
        res.addTransformation(platform, fullName);
        this.updateSubject();
    }

    /**
     * Update the state of a transformation so its corresponding menu entry gets updated
     * @param {string} csarId
     * @param {string} platform
     * @param {TransformationResponse.StateEnum} state
     */
    public updateTransformationState(csarId: string, platform: string, state: StateEnum) {
        const res = this.dataStore.csars.find(csar => csar.name === csarId);
        res.transformations.find(t => t.platform === platform).state = state;
        this.updateSubject();
    }

    private updateSubject() {
        this._csars.next((Object.assign({}, this.dataStore).csars));
    }

    /**
     * load csars for a transformation
     * @param {string} csarId
     * @returns {Observable<Transformation[]>}
     */
    private getTransformations(csarId: string): Observable<Transformation[]> {
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


    public getCsarByName(csarId: string)  {
        return this.csarService.getCSARInfoUsingGET(csarId).map(async data => {
            const res = await this.addTransformationsToCsars([data]);
            return res[0];
        }, err => {
            this.messageService.addErrorMessage(`Failed to load the csar "${csarId}"`);
        });
    }

    public getCsarById(csarId: string): Csar {
        return this.dataStore.csars.find(csar => csar.name === csarId);
    }

    public getCsarLogs(csarId: string, last: number): Observable<LogResponse> {
        return this.csarService.getLogsUsingGET(csarId, last);
    }
}
