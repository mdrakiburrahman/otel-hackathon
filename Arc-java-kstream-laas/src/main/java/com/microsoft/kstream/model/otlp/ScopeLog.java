
package com.microsoft.kstream.model.otlp;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ScopeLog implements Serializable
{

    @SerializedName("scope")
    @Expose
    private Scope scope;
    @SerializedName("logRecords")
    @Expose
    private List<LogRecord> logRecords = null;
    private final static long serialVersionUID = -1301937542159328230L;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScopeLog.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scope");
        sb.append('=');
        sb.append(((this.scope == null)?"<null>":this.scope));
        sb.append(',');
        sb.append("logRecords");
        sb.append('=');
        sb.append(((this.logRecords == null)?"<null>":this.logRecords));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
