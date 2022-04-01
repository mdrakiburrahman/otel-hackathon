######## Variables #################
AT := @

NAMESPACE := test# <----- CHANGE THIS 
CLUSTER_IP := 10.91.136.152# <---- CHANGE THIS
CONTAINER_CERTS_DIR := "/var/run/secrets/managed/certificates"

MAKEFILE_PATH := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
CERTS_DIR := $(MAKEFILE_PATH)certs

OTEL_CONFIGMAP := otel-cluster-config

KCAT_SSL_ARGS := -X security.protocol=SSL -X ssl.certificate.location=$(CERTS_DIR)/certificate.pem -X ssl.key.location=$(CERTS_DIR)/key.pem -X ssl.ca.location=$(CERTS_DIR)/ca.crt

TOPIC ?= otel.node

######## Commands you care about ##

deploy: config
	kubectl apply -f ./otel_agent.yaml -n $(NAMESPACE)
	kubectl apply -f ./otel_collector.yaml -n $(NAMESPACE)

read-topic: ssl-files
	$(AT)kafkacat -b $(CLUSTER_IP):31052 -t $(TOPIC) $(KCAT_SSL_ARGS)

list-topics: ssl-files
	$(AT)kafkacat -b $(CLUSTER_IP):31052 -L $(KCAT_SSL_ARGS)

.PHONY: read-topic list-topics deploy logs

######## Generate Configmap #######

config: ssl-files 
	$(AT)kubectl delete configmap -n $(NAMESPACE) $(OTEL_CONFIGMAP) || true
	kubectl create configmap $(OTEL_CONFIGMAP) -n $(NAMESPACE) --from-file=$(CERTS_DIR)/ca.crt --from-file=$(CERTS_DIR)/key.pem --from-file=$(CERTS_DIR)/certificate.pem

.PHONY: config

######## Generate Certs ###########

ssl-files: certs/certificate.pem certs/key.pem certs/ca.crt

certs:
	mkdir -p certs

certs/certificate.pem: | certs
	$(AT)kubectl cp -n $(NAMESPACE) -c kafka-broker kafka-broker-0:$(CONTAINER_CERTS_DIR)/kafka-broker/kafka-broker-certificate.pem $(CERTS_DIR)/certificate.pem

certs/key.pem: | certs
	$(AT)kubectl cp -n $(NAMESPACE) -c kafka-broker kafka-broker-0:$(CONTAINER_CERTS_DIR)/kafka-broker/kafka-broker-privatekey.pem $(CERTS_DIR)/key.pem

certs/ca.crt: | certs
	$(AT)kubectl get configmap -n $(NAMESPACE) cluster-configmap -o=jsonpath='{.data.cluster-ca-certificate\.crt}' >> $(CERTS_DIR)/ca.crt

.PHONY: ssl-files

######## Clean #####################

nuke: clean deploy-clean

clean:
	rm -rf $(CERTS_DIR)

deploy-clean:
	kubectl delete -f $(MAKEFILE_PATH)/otel_agent.yaml -n $(NAMESPACE) || true
	kubectl delete -d $(MAKEFILE_PATH)/otel_collector.yaml -n $(NAMESPACE) || true
	kubectl delete configmap -n $(NAMESPACE) $(OTEL_CONFIGMAP) || true
	kubectl delete deployment -n $(NAMESPACE) otel-collector || true

debug:
	$(AT)echo "$(MAKEFILE_PATH)"

.PHONY: clean deploy-clean debug nuke
