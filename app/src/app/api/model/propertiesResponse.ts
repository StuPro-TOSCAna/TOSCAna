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
import { Link } from './link';
import { PropertyWrap } from './propertyWrap';


export interface PropertiesResponse {
    links?: Array<Link>;
    /**
     * The list of properties associated with this transformation.
     */
    properties: Array<PropertyWrap>;
}
