package com.microsoft.kstream;

import com.microsoft.kstream.model.otlp.*;
import com.microsoft.kstream.model.dbaas.*;

import java.util.ArrayList;
import java.util.List;

class RecordBuilder {
    /**
     * Create a flattened List of LaaS friendly records from resourceLogs
     * Each Log Record is denormalized into an independent record
     *
     * @param invoice PosInvoice object
     * @return List of LaasJSON
     */
    static List<LaasJSON> getDbaasRecords(OtlpJSON otlp) {
        List<LaasJSON> records = new ArrayList<>();
        
        for (ResourceLog resourceLog : otlp.getResourceLogs()) {
            for (ScopeLog scopeLog : resourceLog.getScopeLogs()) {
                for (LogRecord logRecord : scopeLog.getLogRecords()) {
                    // Create a new LaasJSON object
                    LaasJSON record = new LaasJSON();

                    // Private
                    Private p = new Private();

                    // Loop over each attribute to grab the key/value pairs
                    for (Attribute attribute : logRecord.getAttributes()) {
                        switch (attribute.getKey()) {
                            case "custom_resource_name":
                                p.setCustomResourceName(attribute.getValue().getStringValue());
                                break;
                            case "es_process_timestamp":
                                p.setEsProcessTimestamp(attribute.getValue().getStringValue());
                                break;
                            case "file_path":
                                p.setFilePath(attribute.getValue().getStringValue());
                                break;
                            case "fluent.tag":
                                p.setFluentTag(attribute.getValue().getStringValue());
                                break;
                            case "kubernetes_container_name":
                                p.setKubernetesContainerName(attribute.getValue().getStringValue());
                                break;
                            case "kubernetes_namespace":
                                p.setKubernetesNamespace(attribute.getValue().getStringValue());
                                break;
                            case "kubernetes_node_name":
                                p.setKubernetesNodeName(attribute.getValue().getStringValue());
                                break;
                            case "kubernetes_pod_name":
                                p.setKubernetesPodName(attribute.getValue().getStringValue());
                                break;
                        }
                    }

                    // Raw
                    Raw raw = new Raw();
                    raw.setBody(logRecord.getBody().getStringValue());

                    // Std
                    Std std = new Std();
                    std.setAppcode(p.getKubernetesPodName().substring(0, 4)); // For demo, assumes first 4 letters are the appcode
                    std.setEnvironment("DEV");
                    std.setHostname(p.getKubernetesPodName());
                    std.setSource("fluentbit");
                    std.setSeverity(getSeverity(raw.getBody()));
                    std.setTimestamp(logRecord.getTimeUnixNano());
                    std.setMessage(raw.getBody());

                    // Laas
                    Laas laas = new Laas();
                    laas.setAppcode(std.getAppcode()); 
                    laas.setTimestamp(p.getEsProcessTimestamp());

                    // Pipe all into record and add it to List
                    record.setPrivate(p);
                    record.setRaw(raw);
                    record.setStd(std);
                    record.setLaas(laas);

                    records.add(record);
                }
            }
        }
        return records;
    }    

    static String getSeverity(String log){
        
        if (log.contains("[FATAL]")){
            return "FATAL";
        }
        if (log.contains("[ERROR]")){
            return "ERROR";
        }
        if (log.contains("[WARN]")){
            return "WARN";
        }
        if (log.contains("[DEBUG]")){
            return "DEBUG";
        }

        return "INFO";
    }

}
