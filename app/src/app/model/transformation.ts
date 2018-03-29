import {LifecyclePhase, TransformationResponse} from '../api';

export interface TransformationInterface extends TransformationResponse {
    fullName: string;
}

export class Transformation implements TransformationInterface {
    constructor(fullName: string, phases: Array<LifecyclePhase>, platform: string, state: TransformationResponse.StateEnum) {
        this.fullName = fullName;
        this.phases = phases;
        this.platform = platform;
        this.state = state;
    }

    fullName: string;
    phases: Array<LifecyclePhase>;
    platform: string;
    state: TransformationResponse.StateEnum;

    phaseIsActive(phase: LifecyclePhase.StateEnum) {
        return phase === LifecyclePhase.StateEnum.EXECUTING;
    }
}
