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
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent }                           from '@angular/common/http';
import { CustomHttpUrlEncodingCodec }                        from '../encoder';

import { Observable }                                        from 'rxjs/Observable';

import { GetInputsResponse } from '../model/getInputsResponse';
import { GetOutputsResponse } from '../model/getOutputsResponse';
import { InputsResponse } from '../model/inputsResponse';
import { LogResponse } from '../model/logResponse';
import { ResponseEntity } from '../model/responseEntity';
import { RestErrorResponse } from '../model/restErrorResponse';
import { TransformationResources } from '../model/transformationResources';
import { TransformationResponse } from '../model/transformationResponse';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class TransformationsService {

    protected basePath = '';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (basePath) {
            this.basePath = basePath;
        }
        if (configuration) {
            this.configuration = configuration;
            this.basePath = basePath || configuration.basePath || this.basePath;
        }
    }

    /**
     * @param consumes string[] mime-types
     * @return true: consumes contains 'multipart/form-data', false: otherwise
     */
    private canConsumeForm(consumes: string[]): boolean {
        const form = 'multipart/form-data';
        for (let consume of consumes) {
            if (form === consume) {
                return true;
            }
        }
        return false;
    }


    /**
     * Create a new TransformationInterface
     * Creates a new transformation for the given CSAR and Platform (If the platform does not exist and there is no other transformation with the same CSAR and Platform, you have to removeMessage the old transformation in this case)
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public addTransformationUsingPOST(csarId: string, platform: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public addTransformationUsingPOST(csarId: string, platform: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public addTransformationUsingPOST(csarId: string, platform: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public addTransformationUsingPOST(csarId: string, platform: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling addTransformationUsingPOST.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling addTransformationUsingPOST.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];

        return this.httpClient.post<any>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/create`,
            null,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Create a new TransformationInterface
     * Creates a new transformation for the given CSAR and Platform (If the platform does not exist and there is no other transformation with the same CSAR and Platform, you have to removeMessage the old transformation in this case)
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public addTransformationUsingPUT(csarId: string, platform: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public addTransformationUsingPUT(csarId: string, platform: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public addTransformationUsingPUT(csarId: string, platform: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public addTransformationUsingPUT(csarId: string, platform: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling addTransformationUsingPUT.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling addTransformationUsingPUT.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];

        return this.httpClient.put<any>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/create`,
            null,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Delete a transformation
     * Deletes a transformation and all the coresponding artifacts
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public deleteTransformationUsingDELETE(csarId: string, platform: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public deleteTransformationUsingDELETE(csarId: string, platform: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public deleteTransformationUsingDELETE(csarId: string, platform: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public deleteTransformationUsingDELETE(csarId: string, platform: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling deleteTransformationUsingDELETE.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling deleteTransformationUsingDELETE.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.delete<any>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/delete`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Get details for a specific transformation
     * Returns a HAL-Resource Containing the details for the transformation with the given parameters
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getCSARTransformationUsingGET(csarId: string, platform: string, observe?: 'body', reportProgress?: boolean): Observable<TransformationResponse>;
    public getCSARTransformationUsingGET(csarId: string, platform: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<TransformationResponse>>;
    public getCSARTransformationUsingGET(csarId: string, platform: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<TransformationResponse>>;
    public getCSARTransformationUsingGET(csarId: string, platform: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling getCSARTransformationUsingGET.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling getCSARTransformationUsingGET.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<TransformationResponse>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * List all transformations of a CSAR
     * Returns a HAL-Resources list containing all Transformations for a specific CSAR
     * @param csarId The unique identifier for the CSAR
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getCSARTransformationsUsingGET(csarId: string, observe?: 'body', reportProgress?: boolean): Observable<TransformationResources>;
    public getCSARTransformationsUsingGET(csarId: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<TransformationResources>>;
    public getCSARTransformationsUsingGET(csarId: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<TransformationResources>>;
    public getCSARTransformationsUsingGET(csarId: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling getCSARTransformationsUsingGET.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<TransformationResources>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Retrieve the inputs of this transformation
     * This Operation returns a list of inputs, specific to the csar and the platform. If the transformations-inputs is invalid it has to be set in order to proceed with starting the transformation. Setting the inputs is done with a POST or PUT to the same URL (See Set Inputs Operation). If TransformationInterface does not have any inputs, an empty array is returned
     * @param csarId The identifier for the CSAR
     * @param platform The identifier for the platform
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getInputsUsingGET(csarId: string, platform: string, observe?: 'body', reportProgress?: boolean): Observable<GetInputsResponse>;
    public getInputsUsingGET(csarId: string, platform: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<GetInputsResponse>>;
    public getInputsUsingGET(csarId: string, platform: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<GetInputsResponse>>;
    public getInputsUsingGET(csarId: string, platform: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling getInputsUsingGET.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling getInputsUsingGET.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<GetInputsResponse>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/inputs`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Retrieve the outputs and their values
     * This operation returns the outputs of a deployment. Retrieval of the outputs is not possible if the transformation (including deployment) is not done yet
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getOutputsUsingGET(csarId: string, platform: string, observe?: 'body', reportProgress?: boolean): Observable<GetOutputsResponse>;
    public getOutputsUsingGET(csarId: string, platform: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<GetOutputsResponse>>;
    public getOutputsUsingGET(csarId: string, platform: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<GetOutputsResponse>>;
    public getOutputsUsingGET(csarId: string, platform: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling getOutputsUsingGET.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling getOutputsUsingGET.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<GetOutputsResponse>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/outputs`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Download the target artifact archive
     * Once the transformation is done (in the state DONE) or it has encountered a error (state ERROR). It is possible to download a archive (ZIP format) of all the files generated while the transformation was running.
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getTransformationArtifactUsingGET(csarId: string, platform: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public getTransformationArtifactUsingGET(csarId: string, platform: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public getTransformationArtifactUsingGET(csarId: string, platform: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public getTransformationArtifactUsingGET(csarId: string, platform: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling getTransformationArtifactUsingGET.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling getTransformationArtifactUsingGET.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/octet-stream'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<any>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/artifact`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Get the logEntries for a TransformationInterface
     * Returns the logEntries for a transformation, starting at a specific position. from the given start index all following log lines get returned. If the start index is larger than the current last log index the operation will return a empty list.
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param start The index of the first log entry you want (0 returns the whole log)
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getTransformationLogsUsingGET(csarId: string, platform: string, start: number, observe?: 'body', reportProgress?: boolean): Observable<LogResponse>;
    public getTransformationLogsUsingGET(csarId: string, platform: string, start: number, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<LogResponse>>;
    public getTransformationLogsUsingGET(csarId: string, platform: string, start: number, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<LogResponse>>;
    public getTransformationLogsUsingGET(csarId: string, platform: string, start: number, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling getTransformationLogsUsingGET.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling getTransformationLogsUsingGET.');
        }
        if (start === null || start === undefined) {
            throw new Error('Required parameter start was null or undefined when calling getTransformationLogsUsingGET.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (start !== undefined) {
            queryParameters = queryParameters.set('start', <any>start);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
        ];

        return this.httpClient.get<LogResponse>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/logs`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Set the value of inputs
     * With this method it is possible to set the value of an transformations-inputs or multiple inputs at once. The values of inputs can be set as long as they are in the READY or INPUT_REQUIRED state. The transformation changes its state to ready once all required inputs have a valid value assigned to them.
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param propertiesRequest propertiesRequest
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public setInputsUsingPOST(csarId: string, platform: string, propertiesRequest: InputsResponse, observe?: 'body', reportProgress?: boolean): Observable<InputsResponse>;
    public setInputsUsingPOST(csarId: string, platform: string, propertiesRequest: InputsResponse, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<InputsResponse>>;
    public setInputsUsingPOST(csarId: string, platform: string, propertiesRequest: InputsResponse, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<InputsResponse>>;
    public setInputsUsingPOST(csarId: string, platform: string, propertiesRequest: InputsResponse, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling setInputsUsingPOST.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling setInputsUsingPOST.');
        }
        if (propertiesRequest === null || propertiesRequest === undefined) {
            throw new Error('Required parameter propertiesRequest was null or undefined when calling setInputsUsingPOST.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];
        let httpContentTypeSelected:string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set("Content-Type", httpContentTypeSelected);
        }

        return this.httpClient.post<InputsResponse>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/inputs`,
            propertiesRequest,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Set the value of inputs
     * With this method it is possible to set the value of an transformations-inputs or multiple inputs at once. The values of inputs can be set as long as they are in the READY or INPUT_REQUIRED state. The transformation changes its state to ready once all required inputs have a valid value assigned to them.
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param propertiesRequest propertiesRequest
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public setInputsUsingPUT(csarId: string, platform: string, propertiesRequest: InputsResponse, observe?: 'body', reportProgress?: boolean): Observable<InputsResponse>;
    public setInputsUsingPUT(csarId: string, platform: string, propertiesRequest: InputsResponse, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<InputsResponse>>;
    public setInputsUsingPUT(csarId: string, platform: string, propertiesRequest: InputsResponse, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<InputsResponse>>;
    public setInputsUsingPUT(csarId: string, platform: string, propertiesRequest: InputsResponse, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling setInputsUsingPUT.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling setInputsUsingPUT.');
        }
        if (propertiesRequest === null || propertiesRequest === undefined) {
            throw new Error('Required parameter propertiesRequest was null or undefined when calling setInputsUsingPUT.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];
        let httpContentTypeSelected:string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set("Content-Type", httpContentTypeSelected);
        }

        return this.httpClient.put<InputsResponse>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/inputs`,
            propertiesRequest,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Start a TransformationInterface
     * Starts a transformation that has been created and is ready to get started. To start a transformation, the TransformationInterface has to be in the state READY otherwise the transformation cannot start.
     * @param csarId The unique identifier for the CSAR
     * @param platform The identifier for the platform
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public startTransformationUsingPOST(csarId: string, platform: string, observe?: 'body', reportProgress?: boolean): Observable<ResponseEntity>;
    public startTransformationUsingPOST(csarId: string, platform: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<ResponseEntity>>;
    public startTransformationUsingPOST(csarId: string, platform: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<ResponseEntity>>;
    public startTransformationUsingPOST(csarId: string, platform: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (csarId === null || csarId === undefined) {
            throw new Error('Required parameter csarId was null or undefined when calling startTransformationUsingPOST.');
        }
        if (platform === null || platform === undefined) {
            throw new Error('Required parameter platform was null or undefined when calling startTransformationUsingPOST.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/hal+json'
        ];
        let httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set("Accept", httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];

        return this.httpClient.post<ResponseEntity>(`${this.basePath}/api/csars/${encodeURIComponent(String(csarId))}/transformations/${encodeURIComponent(String(platform))}/start`,
            null,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
