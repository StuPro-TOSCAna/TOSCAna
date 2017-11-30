package org.opentosca.toscana.cli.commands;

/**
 Strings that are used in the CLI, for easier test usage declared here
 */
public final class Constants {

    public static final String PARAM_CSAR = "<CSAR>";
    public static final String PARAM_PLATFORM = "<Platform>";
    public static final String SOMETHING_WRONG = "Something went wrong while ";
    public static final String ERROR_PLACEHOLDER = " %s";
    public static final String SERVER_ERROR_PLACEHOLDER = " %s %s";
    public static final String CSAR_UPLOAD_IO_ERROR = "loading Csar:";
    public static final String CSAR_UPLOAD_RESPONSE_ERROR = "uploading Csar:";
    public static final String CSAR_DELETE_ERROR = "deleting Csar:";
    public static final String CSAR_LIST_EMPTY = "List of CSARs is empty.";
    public static final String CSAR_LIST_ERROR = "loading Csar List:";
    public static final String CSAR_INFO_EMPTY = "No Information about the CSAR found.";
    public static final String CSAR_INFO_ERROR = "getting CSAR Information:";
    public static final String TRANSFORMATION_CREATE_ERROR = "creating CSAR:";
    public static final String TRANSFORMATION_START_ERROR = "starting the Transformation:";
    public static final String TRANSFORMATION_STOP = "Aborting Transformation.";
    public static final String TRANSFORMATION_DELETE_ERROR = "deleting the Transformation:";
    public static final String TRANSFORMATION_DOWNLOAD_ERROR = "getting the Transformation Artifact:";
    public static final String TRANSFORMATION_LIST_EMPTY = "List of Transformations available for CSAR empty.";
    public static final String TRANSFORMATION_LIST_ERROR = "getting the Transformation List:";
    public static final String TRANSFORMATION_INFO_EMPTY = "Platform not known.";
    public static final String TRANSFORMATION_INFO_ERROR = "getting Transformation Information:";
    public static final String TRANSFORMATION_LOGS_EMPTY = "No Logs found.";
    public static final String TRANSFORMATION_LOGS_ERROR = "getting the Transformation Logs:";
    public static final String INPUT_LIST_EMPTY = "No required Inputs found.";
    public static final String INPUT_LIST_ERROR = "getting the Transformation Inputs:";
    public static final String INPUT_SET_ERROR = "setting the Transformation Inputs:";
    public static final String PLATFORM_LIST_EMPTY = "List of Platforms is empty.";
    public static final String PLATFORM_LIST_ERROR = "getting the Platform List:";
    public static final String PLATFORM_INFO_EMPTY = "No ID found.";
    public static final String PLATFORM_INFO_ERROR = "getting Platform Information:";
    public static final String STATUS_EMPTY = "No Status found.";
    public static final String STATUS_ERROR = "getting the System Status:";
    public static final String NOT_PROVIDED = "CSAR and/ or Platform not provided, please specify!";
}
