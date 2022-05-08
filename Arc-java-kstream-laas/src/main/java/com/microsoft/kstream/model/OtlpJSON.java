
package com.microsoft.kstream.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OtlpJSON implements Serializable
{

    @SerializedName("resourceLogs")
    @Expose
    private List<ResourceLog> resourceLogs = null;
    private final static long serialVersionUID = -195163773006395008L;

    public List<ResourceLog> getResourceLogs() {
        return resourceLogs;
    }

    public void setResourceLogs(List<ResourceLog> resourceLogs) {
        this.resourceLogs = resourceLogs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OtlpJSON.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("resourceLogs");
        sb.append('=');
        sb.append(((this.resourceLogs == null)?"<null>":this.resourceLogs));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
