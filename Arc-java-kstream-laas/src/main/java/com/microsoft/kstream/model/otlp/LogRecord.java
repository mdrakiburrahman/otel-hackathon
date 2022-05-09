
package com.microsoft.kstream.model.otlp;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class LogRecord implements Serializable
{

    @SerializedName("timeUnixNano")
    @Expose
    private String timeUnixNano;
    @SerializedName("body")
    @Expose
    private Body body;
    @SerializedName("attributes")
    @Expose
    private List<Attribute> attributes = null;
    @SerializedName("traceId")
    @Expose
    private String traceId;
    @SerializedName("spanId")
    @Expose
    private String spanId;
    private final static long serialVersionUID = -363597920151336579L;

    public String getTimeUnixNano() {
        return timeUnixNano;
    }

    public void setTimeUnixNano(String timeUnixNano) {
        this.timeUnixNano = timeUnixNano;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LogRecord.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("timeUnixNano");
        sb.append('=');
        sb.append(((this.timeUnixNano == null)?"<null>":this.timeUnixNano));
        sb.append(',');
        sb.append("body");
        sb.append('=');
        sb.append(((this.body == null)?"<null>":this.body));
        sb.append(',');
        sb.append("attributes");
        sb.append('=');
        sb.append(((this.attributes == null)?"<null>":this.attributes));
        sb.append(',');
        sb.append("traceId");
        sb.append('=');
        sb.append(((this.traceId == null)?"<null>":this.traceId));
        sb.append(',');
        sb.append("spanId");
        sb.append('=');
        sb.append(((this.spanId == null)?"<null>":this.spanId));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
