/**
 * TOSCAna
 * To be Done!
 *
 * OpenAPI spec version: 1.0.0-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { InputWrap } from './inputWrap';


export interface GetInputsResponse {
    /**
     * The list of properties associated with this transformation, if this list is empty, the transformation doesn't have any properties to set.
     */
    inputs: Array<InputWrap>;
}
