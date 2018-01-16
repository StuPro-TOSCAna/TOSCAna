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


export interface TransformationResponse {
    /**
     * The platform identifier for this transformation
     */
    platform: string;
    /**
     * The progress in % of how much is done to complete the transformation
     */
    progress: number;
    /**
     * The Current State of the transformation. Has to be one of the following: \"READY\", \"INPUT_REQUIRED\", \"TRANSFORMING\", \"DONE\" or \"ERROR\"
     */
    status: string;
}