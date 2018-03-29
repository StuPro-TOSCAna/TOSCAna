import {LifecyclePhase} from '../api';
import StateEnum = LifecyclePhase.StateEnum;

export function getColorForLifecyclePhase(phase: LifecyclePhase) {
    let res = '';
    if (phase.state === StateEnum.DONE) {
        res = 'text-success';
    } else if (phase.state === StateEnum.FAILED) {
        res = 'text-danger';
    } else if (phase.state === StateEnum.EXECUTING) {
        res = 'text-warning';
    } else if (phase.state === StateEnum.PENDING) {
        res = 'text-primary';
    } else if (phase.state === StateEnum.SKIPPING) {
        res = 'text-secondary';
    }
    return res + ' state';
}

