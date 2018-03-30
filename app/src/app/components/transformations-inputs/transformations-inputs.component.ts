import {Component, OnInit} from '@angular/core';
import {InputWrap, TransformationResponse} from '../../api';
import {ClientsTransformationsService} from '../../services/transformations.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {ClientCsarsService} from '../../services/csar.service';
import {Csar} from '../../model/csar';
import {RouteHandler} from '../../services/route.service';
import {ClientPlatformsService} from '../../services/platforms.service';
import {TransformationInterface} from '../../model/transformation';
import {isNullOrUndefined} from 'util';
import {MessageService} from '../../services/message.service';
import StateEnum = TransformationResponse.StateEnum;
import TypeEnum = InputWrap.TypeEnum;

@Component({
    selector: 'app-transformation-inputs',
    templateUrl: './transformations-inputs.component.html',
    styleUrls: ['./transformations-inputs.component.scss']
})
export class TransformationInputsComponent implements OnInit {
    selectedPlatform: string;
    csarId: string;
    inputs: InputWrap[] = [];
    errorMsg = false;
    csar: Csar;
    transformation: TransformationInterface;
    everythingValid = true;

    constructor(private messageService: MessageService, private routeHandler: RouteHandler,
                private transformationsProvider: ClientsTransformationsService,
                private csarsProvider: ClientCsarsService,
                private platformsProvider: ClientPlatformsService,
                private route: ActivatedRoute) {
    }

    /**
     * checks if an transformation input is valid
     * @param {InputWrap} item
     * @returns {boolean} inputValid
     */
    validateInput(item: InputWrap) {
        function isNumber(value: string) {
            return !isNaN(Number(value));
        }
        switch (item.type) {
            case TypeEnum.Boolean:
                const val = item.value.toLowerCase().trim();
                if (val === 'true' || val === 'false') {
                    return true;
                }
                break;
            case TypeEnum.Integer:
                const containsNoPointOrComma = item.value.indexOf('.') === -1 && item.value.indexOf(',') === -1;
                return isNumber(item.value) && containsNoPointOrComma;
            case TypeEnum.Float:
                return isNumber(item.value);
            case TypeEnum.UnsignedInteger:
                if (!(item.value.charAt(1) === '-')) {
                    return true;
                }
                break;
            default:
                return true;
        }
        return false;
    }

    /**
     * triggered on input changes
     * triggers the validation of the input
     * and the check if it is possible to submitInputs the inputs
     * @param {InputWrap} item
     * @param {string} input
     */
    onInputChange(item: InputWrap, input: string) {
        item.value = input;
        const parse = this.validateInput(item);

        item.valid = !(item.required && ((item.value === null || item.value === '')));
        if (!parse) {
            item.valid = false;
        }
        this.checkIfEverythingIsValid();
    }

    hasTypeBoolean(item: InputWrap) {
        return item.type === TypeEnum.Boolean;
    }

    async submitInputs() {
        await this.transformationsProvider.setTransformationProperties(this.csarId, this.selectedPlatform, this.inputs).then(result => {
            this.startTransformation();
        }, err => {
            if (err.status === 406) {
                this.inputs = err.error.inputs;
            }
            this.errorMsg = true;
        });
    }

    async ngOnInit() {
        this.route.paramMap.switchMap((params: ParamMap) => {
            this.csarId = params.get('csar');
            this.selectedPlatform = params.get('platform');
            this.transformationsProvider.getTransformationByCsarAndPlatform(this.csarId, this.selectedPlatform).subscribe(data => {
                if (data.state !== StateEnum.INPUTREQUIRED && data.state !== StateEnum.READY) {
                    this.routeHandler.openTransformation(this.csarId, this.selectedPlatform);
                }
            });
            return this.transformationsProvider.getTransformationInputs(this.csarId, this.selectedPlatform);
        }).subscribe(data => {
            this.inputs = data.inputs;
            this.checkIfEverythingIsValid();
        }, err => this.messageService.addErrorMessage('Failed to load inputs'));
    }

    private checkIfEverythingIsValid() {
        this.everythingValid = true;
        for (const property of this.inputs) {
            if (!property.valid) {
                this.everythingValid = false;
            }
        }
    }

    startTransformation() {
        this.csar = this.csarsProvider.getCsarById(this.csarId);
        this.transformationsProvider.startTransformation(this.csar.name, this.selectedPlatform).then(() => {
            this.csar.addTransformation(this.selectedPlatform, this.platformsProvider.getFullPlatformName(this.selectedPlatform));
            this.csarsProvider.updateCsar(this.csar);
            this.routeHandler.openTransformation(this.csar.name, this.selectedPlatform);
        });
    }

    /**
     * returns the default value is available
     * @param {string} defaultValue
     * @returns {string} empty string or default value
     */
    getDefaultValue(defaultValue: string) {
        if (isNullOrUndefined(defaultValue)) {
            return '';
        } else {
            return defaultValue;
        }
    }

    /**
     * sets the boolean if a checkbox is checked
     * @param {string} key of the boolean input
     * @param {boolean} checked
     */
    convertAndSetBool(key: string, checked: boolean) {
        this.inputs.find(input => input.key === key).value = String(checked);
    }
}
