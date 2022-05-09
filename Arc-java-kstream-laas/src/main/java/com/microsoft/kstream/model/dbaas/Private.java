
package com.microsoft.kstream.model.dbaas;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Private {

    @SerializedName("custom_resource_name")
    @Expose
    private String customResourceName;
    @SerializedName("es_process_timestamp")
    @Expose
    private String esProcessTimestamp;
    @SerializedName("file_path")
    @Expose
    private String filePath;
    @SerializedName("fluent.tag")
    @Expose
    private String fluentTag;
    @SerializedName("kubernetes_container_name")
    @Expose
    private String kubernetesContainerName;
    @SerializedName("kubernetes_namespace")
    @Expose
    private String kubernetesNamespace;
    @SerializedName("kubernetes_node_name")
    @Expose
    private String kubernetesNodeName;
    @SerializedName("kubernetes_pod_name")
    @Expose
    private String kubernetesPodName;
    @SerializedName("service_name")
    @Expose
    private String serviceName;

    public String getCustomResourceName() {
        return customResourceName;
    }

    public void setCustomResourceName(String customResourceName) {
        this.customResourceName = customResourceName;
    }

    public String getEsProcessTimestamp() {
        return esProcessTimestamp;
    }

    public void setEsProcessTimestamp(String esProcessTimestamp) {
        this.esProcessTimestamp = esProcessTimestamp;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFluentTag() {
        return fluentTag;
    }

    public void setFluentTag(String fluentTag) {
        this.fluentTag = fluentTag;
    }

    public String getKubernetesContainerName() {
        return kubernetesContainerName;
    }

    public void setKubernetesContainerName(String kubernetesContainerName) {
        this.kubernetesContainerName = kubernetesContainerName;
    }

    public String getKubernetesNamespace() {
        return kubernetesNamespace;
    }

    public void setKubernetesNamespace(String kubernetesNamespace) {
        this.kubernetesNamespace = kubernetesNamespace;
    }

    public String getKubernetesNodeName() {
        return kubernetesNodeName;
    }

    public void setKubernetesNodeName(String kubernetesNodeName) {
        this.kubernetesNodeName = kubernetesNodeName;
    }

    public String getKubernetesPodName() {
        return kubernetesPodName;
    }

    public void setKubernetesPodName(String kubernetesPodName) {
        this.kubernetesPodName = kubernetesPodName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Private.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("customResourceName");
        sb.append('=');
        sb.append(((this.customResourceName == null)?"<null>":this.customResourceName));
        sb.append(',');
        sb.append("esProcessTimestamp");
        sb.append('=');
        sb.append(((this.esProcessTimestamp == null)?"<null>":this.esProcessTimestamp));
        sb.append(',');
        sb.append("filePath");
        sb.append('=');
        sb.append(((this.filePath == null)?"<null>":this.filePath));
        sb.append(',');
        sb.append("fluentTag");
        sb.append('=');
        sb.append(((this.fluentTag == null)?"<null>":this.fluentTag));
        sb.append(',');
        sb.append("kubernetesContainerName");
        sb.append('=');
        sb.append(((this.kubernetesContainerName == null)?"<null>":this.kubernetesContainerName));
        sb.append(',');
        sb.append("kubernetesNamespace");
        sb.append('=');
        sb.append(((this.kubernetesNamespace == null)?"<null>":this.kubernetesNamespace));
        sb.append(',');
        sb.append("kubernetesNodeName");
        sb.append('=');
        sb.append(((this.kubernetesNodeName == null)?"<null>":this.kubernetesNodeName));
        sb.append(',');
        sb.append("kubernetesPodName");
        sb.append('=');
        sb.append(((this.kubernetesPodName == null)?"<null>":this.kubernetesPodName));
        sb.append(',');
        sb.append("serviceName");
        sb.append('=');
        sb.append(((this.serviceName == null)?"<null>":this.serviceName));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
