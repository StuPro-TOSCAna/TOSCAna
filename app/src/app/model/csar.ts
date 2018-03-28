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
        const validStates = [StateEnum.DONE, StateEnum.SKIPPING];
        return this.checkLifecyclePhasesForGivenStates(validStates);
    }

    /**
     * check if all of the lifecycle phases are in one of the given states
     * @param validStates Array of lifecycle phase states
     * @returns {boolean}
     */
    private checkLifecyclePhasesForGivenStates(validStates) {
        let valid = true;
        this.phases.forEach(phase => {
            if (!(validStates.indexOf(phase.state) !== -1)) {
                valid = false;
                return;
            }
        });
        return valid;
    }

    currentlyParsing() {
        const invalidPhases = [StateEnum.EXECUTING, StateEnum.PENDING];
        return this.checkLifecyclePhasesForGivenStates(invalidPhases);
    }

    public addTransformation(platform: string, platformFullName: string) {
        const transformation: Transformation = new Transformation(platformFullName, null, platform, TransformationStateEnum.INPUTREQUIRED);
        const existingTransformation = this.transformations.find(item => item.platform === platform);
        if (!isNullOrUndefined(existingTransformation)) {
            this.transformations.splice(this.transformations.indexOf(existingTransformation), 1);
        }
        this.transformations.push(transformation);
    }
}
