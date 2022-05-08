
package com.microsoft.kstream.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class LogRecord {

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

}
