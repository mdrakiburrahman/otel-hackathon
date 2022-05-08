
package com.microsoft.kstream.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ResourceLog {

    @SerializedName("resource")
    @Expose
    private Resource resource;
    @SerializedName("scopeLogs")
    @Expose
    private List<ScopeLog> scopeLogs = null;

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

}
