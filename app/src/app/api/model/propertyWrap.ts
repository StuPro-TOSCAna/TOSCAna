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


export interface PropertyWrap {
    /**
     * the default value of the property
     */
    defaultValue: string;
    /**
     * The description that should be displayed on the Frontend to describe the key
     */
    description: string;
    /**
     * the unique key for a property
     */
    key: string;
    /**
     * Determines if the property is required to start the transformation
     */
    required: boolean;
    /**
     * The 'Datatype' of a property. Supported Types are 'name', 'text', 'boolean', 'integer', 'unsigned_integer', 'float' and 'secret'. If you try to set a value of a non existant key. The type in the response will be 'invalid' however this type only occurs in that case
     */
    type: PropertyWrap.TypeEnum;
    /**
     * true if the property is valid, false otherwise. The server will ignore this value, as it is designed to be read-only.
     */
    valid?: boolean;
    /**
     * gcr.io
     */
    value?: string;
}
export namespace PropertyWrap {
    export type TypeEnum = 'name' | 'text' | 'secret' | 'integer' | 'unsigned_integer' | 'float' | 'boolean' | 'invalid_key';
    export const TypeEnum = {
        Name: 'name' as TypeEnum,
        Text: 'text' as TypeEnum,
        Secret: 'secret' as TypeEnum,
        Integer: 'integer' as TypeEnum,
        UnsignedInteger: 'unsigned_integer' as TypeEnum,
        Float: 'float' as TypeEnum,
        Boolean: 'boolean' as TypeEnum,
        InvalidKey: 'invalid_key' as TypeEnum
    }
}
