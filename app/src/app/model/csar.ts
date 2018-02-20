import {Transformation} from './transformation';
import {isNullOrUndefined} from 'util';
import {LifecyclePhase, TransformationResponse} from '../api';
import TransformationStateEnum = TransformationResponse.StateEnum;
import StateEnum = LifecyclePhase.StateEnum;

export class Csar {
    name: string;
    transformations: Transformation[];
    phases: Array<LifecyclePhase>;

    constructor(name: string, phases: Array<LifecyclePhase>, transformations: Transformation[]) {
        this.name = name;
        if (isNullOrUndefined(transformations)) {
            this.transformations = [];
        } else {
            this.transformations = transformations;
        }
        this.phases = phases;
    }

    successfulParsed() {
        let valid = true;
        const validStates = [StateEnum.DONE, StateEnum.SKIPPING];
        this.phases.forEach(phase => {
            if (!(validStates.indexOf(phase.state) !== -1)) {
                valid = false;
                return;
            }
        });
        return valid;
    }

    public addTransformation(platform: string, platformFullName: string) {
        const transformation: Transformation = {
            platform: platform,
            fullName: platformFullName,
            state: TransformationStateEnum.INPUTREQUIRED,
            phases: null
        };
        const existingTransformation = this.transformations.find(item => item.platform === platform);
        if (!isNullOrUndefined(existingTransformation)) {
            this.transformations.splice(this.transformations.indexOf(existingTransformation), 1);
        }
        this.transformations.push(transformation);
    }
}
