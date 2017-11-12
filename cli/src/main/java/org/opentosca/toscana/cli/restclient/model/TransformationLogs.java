package org.opentosca.toscana.cli.restclient.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TransformationLogs {

    private Integer start;
    private Integer end;
    @SerializedName("logs")
    private List<TransformationLog> logList = new ArrayList<>();

    /**
     *
     * @param logs
     * @param start
     * @param end
     */
    public TransformationLogs(Integer start, Integer end, List<TransformationLog> logList) {
        this.start = start;
        this.end = end;
        this.logList = logList;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public List<TransformationLog> getLogs() {
        return logList;
    }
}
