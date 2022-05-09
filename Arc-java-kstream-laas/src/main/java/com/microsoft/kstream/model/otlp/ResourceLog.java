
package com.microsoft.kstream.model.otlp;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ResourceLog implements Serializable
{

    @SerializedName("resource")
    @Expose
    private Resource resource;
    @SerializedName("scopeLogs")
    @Expose
    private List<ScopeLog> scopeLogs = null;
    private final static long serialVersionUID = -5348565482887091532L;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public List<ScopeLog> getScopeLogs() {
        return scopeLogs;
    }

    public void setScopeLogs(List<ScopeLog> scopeLogs) {
        this.scopeLogs = scopeLogs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ResourceLog.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("resource");
        sb.append('=');
        sb.append(((this.resource == null)?"<null>":this.resource));
        sb.append(',');
        sb.append("scopeLogs");
        sb.append('=');
        sb.append(((this.scopeLogs == null)?"<null>":this.scopeLogs));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
