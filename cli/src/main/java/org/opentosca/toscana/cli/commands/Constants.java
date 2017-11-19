package org.opentosca.toscana.cli.commands;

/**
 Strings that are used in the CLI, for easier test usage declared here
 */
public final class Constants {

    public static final String PARAM_CSAR = "<CSAR>";
    public static final String PARAM_PLATFORM = "<Platform>";
    public final Integer API_PORT = 8084;
    public final String CSAR_UPLOAD_IO_ERROR = "Something went wrong while loading Csar:";
    public final String CSAR_UPLOAD_RESPONSE_ERROR = "Something went wrong while uploading Csar:";
    public final String CSAR_DELETE_ERROR = "Something went wrong while deleting Csar:";
    public final String CSAR_LIST_EMPTY = "List of CSARs is empty.";
    public final String CSAR_LIST_ERROR = "Something went wrong while loading Csar List:";
    public final String CSAR_INFO_EMPTY = "No Information about the CSAR found.";
    public final String CSAR_INFO_ERROR = "Something went wrong while getting CSAR Information:";
    public final String TRANSFORMATION_CREATE_ERROR = "Something went wrong while creating CSAR:";
    public final String TRANSFORMATION_START_ERROR = "Something went wrong while starting the Transformation:";
    public final String TRANSFORMATION_STOP = "Aborting Transformation.";
    public final String TRANSFORMATION_DELETE_ERROR = "Something went wrong while deleting the Transformation:";
    public final String TRANSFORMATION_DOWNLOAD_ERROR = "Something went wrong while getting the Transformation Artifact:";
    public final String TRANSFORMATION_LIST_EMPTY = "List of Transformations available for CSAR ";
    public final String TRANSFORMATION_LIST_ERROR = "Something went wrong while getting the Transformation List:";
    public final String TRANSFORMATION_INFO_EMPTY = "Platform not known.";
    public final String TRANSFORMATION_INFO_ERROR = "Something went wrong while getting Transformation Information:";
    public final String TRANSFORMATION_LOGS_EMPTY = "No Logs found.";
    public final String TRANSFORMATION_LOGS_ERROR = "Something went wrong while getting the Transformation Logs:";
    public final String INPUT_LIST_EMPTY = "No required Inputs found.";
    public final String INPUT_LIST_ERROR = "Something went wrong while getting the Transformation Inputs:";
    public final String INPUT_SET_ERROR = "Something went wrong while setting the Transformation Inputs:";
    public final String PLATFORM_LIST_EMPTY = "List of Platforms is empty.";
    public final String PLATFORM_LIST_ERROR = "Something went wrong while getting the Platform List:";
    public final String PLATFORM_INFO_EMPTY = "No ID found.";
    public final String PLATFORM_INFO_ERROR = "Something went wrong while getting Platform Information:";
    public final String STATUS_EMPTY = "No Status found.";
    public final String STATUS_ERROR = "Something went wrong while getting the System Status:";
    public final String STATUS_METRIC_ERROR = "Something went wrong while getting the System Status:";
    public final String NOT_PROVIDED = "CSAR or Platform not provided, please specify!";
    public final String METHOD_SUCCESS = "success";
}
