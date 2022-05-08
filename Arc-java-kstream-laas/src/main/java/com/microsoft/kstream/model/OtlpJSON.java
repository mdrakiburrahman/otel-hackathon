
package com.microsoft.kstream.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OtlpJSON {

    @SerializedName("resourceLogs")
    @Expose
    private List<ResourceLog> resourceLogs = null;

    public List<ResourceLog> getResourceLogs() {
        return resourceLogs;
    }

    public void setResourceLogs(List<ResourceLog> resourceLogs) {
        this.resourceLogs = resourceLogs;
    }

}
