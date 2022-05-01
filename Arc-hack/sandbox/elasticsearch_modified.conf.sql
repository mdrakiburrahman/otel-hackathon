###############################
# RAKI WAS HERE
###############################
[OUTPUT]
    Name             es
    Match            *
    Host             ${FLUENT_ELASTICSEARCH_HOST}
    Port             ${FLUENT_ELASTICSEARCH_PORT}
    Logstash_Format  On
    Retry_Limit      False
    tls              On
    tls.verify       On
    tls.ca_file      /var/run/configmaps/cluster/cluster-ca-certificate.crt
    tls.crt_file     /var/run/secrets/managed/certificates/fluentbit/fluentbit-certificate.pem
    tls.key_file     /var/run/secrets/managed/certificates/fluentbit/fluentbit-privatekey.pem


