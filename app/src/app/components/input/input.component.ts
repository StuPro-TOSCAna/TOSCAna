import {Component, OnInit} from '@angular/core';
import {InputWrap, TransformationResponse} from '../../api';
import {TransformationsProvider} from '../../providers/transformations/transformations.provider';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {CsarProvider} from '../../providers/csar/csar.provider';
import {Csar} from '../../model/csar';
import {RouteHandler} from '../../handler/route/route.service';
import {PlatformsProvider} from '../../providers/platforms/platforms.provider';
import {Transformation} from '../../model/transformation';
import {isNullOrUndefined} from 'util';
import StateEnum = TransformationResponse.StateEnum;
import TypeEnum = InputWrap.TypeEnum;

@Component({
    selector: 'app-input',
    templateUrl: './input.component.html',
    styleUrls: ['./input.component.scss']
})
export class InputComponent implements OnInit {
    selectedPlatform: string;
    csarId: string;
    invalid = 'invalid';
    inputs: InputWrap[] = [];
    errorMsg = false;
    csar: Csar;
    transformation: Transformation;
    everythingValid = true;

    constructor(private routeHandler: RouteHandler, private transformationsProvider: TransformationsProvider,
                private csarsProvider: CsarProvider,
                private platformsProvider: PlatformsProvider,
                private route: ActivatedRoute) {
    }

    validateProperty(item: InputWrap) {
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

    change(item: InputWrap, input: string) {
        item.value = input;
        const parse = this.validateProperty(item);
        item.valid = true;
        if (item.required && ((item.value === null || item.value === ''))) {
            item.valid = false;
        }
        if (!parse) {
            item.valid = false;
        }
        this.checkIfEverythingIsValid();
    }

    hasTypeBoolean(item: InputWrap) {
        return item.type === TypeEnum.Boolean;
    }

    getClass(item: InputWrap) {
        return item.valid;
    }

    async submit() {
        await this.transformationsProvider.setTransformationProperties(this.csarId, this.selectedPlatform, this.inputs).then(result => {
            this.onSubmit();
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
            return this.transformationsProvider.getTransformationProperties(this.csarId, this.selectedPlatform);
        }).subscribe(data => {
            this.inputs = data.inputs;
            this.checkIfEverythingIsValid();
        }, err => console.log(err));
    }

    private checkIfEverythingIsValid() {
        this.everythingValid = true;
        for (const property of this.inputs) {
            if (!property.valid) {
                this.everythingValid = false;
            }
        }
    }

    onSubmit() {
        this.csar = this.csarsProvider.getCsarById(this.csarId);
        this.transformationsProvider.startTransformation(this.csar.name, this.selectedPlatform).then(() => {
            this.csar.addTransformation(this.selectedPlatform, this.platformsProvider.getFullPlatformName(this.selectedPlatform));
            this.csarsProvider.updateCsar(this.csar);
            this.routeHandler.openTransformation(this.csar.name, this.selectedPlatform);
        });
    }

    getDefaultValue(defaultValue: string) {
        if (isNullOrUndefined(defaultValue)) {
            return '';
        } else {
            return defaultValue;
        }
    }

    convertAndSetBool(key: string, checked: boolean) {
        this.inputs.find(input => input.key === key).value = String(checked);
    }
}
