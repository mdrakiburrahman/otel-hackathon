
package com.microsoft.kstream.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ScopeLog {

    @SerializedName("scope")
    @Expose
    private Scope scope;
    @SerializedName("logRecords")
    @Expose
    private List<LogRecord> logRecords = null;

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public List<LogRecord> getLogRecords() {
        return logRecords;
    }

    public void setLogRecords(List<LogRecord> logRecords) {
        this.logRecords = logRecords;
    }

}
