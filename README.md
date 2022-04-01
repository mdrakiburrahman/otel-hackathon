# Usage instructions

## Deployment

1. Deploy arc local with kakfa: `make deploy-arc-local ENABLE_KAFKA=1`
2. Fill in cluster ip and namespace in makefile
3. Deploy otel agent: `make deploy`
4. Read from kafka: `make read-topic | jq`

## Useful commands

* make deploy : Deploys the otel agent
* make clean : Cleans up all intermediate files
* make deploy-clean: Cleans up all otel related things in the cluster
* make nuke: Clean and deploy-clean
* make read-topic: Reads from the otlp kafka topic
* make list-topics: Lists kafka topics and other information