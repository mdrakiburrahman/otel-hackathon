# Usage instructions

Note for any collection to work other than the agent daemon set you'll need to build and deploy with my arcdata branch `jakedern/otel-hackathon`

## Deployment

1. Deploy arc local with kakfa: `make deploy-arc-local ENABLE_KAFKA=1`
2. Fill in cluster ip and namespace in makefile
3. Deploy otel services: `make deploy`
4. Read from kafka: `make read-topic | jq` or `make read-topic NAME=otel.<fluent|collectd|logs>`

## Useful commands

* make deploy : Deploys the otel agent and collector
* make clean : Cleans up all intermediate files
* make deploy-clean: Cleans up all otel related things in the cluster
* make nuke: Clean and deploy-clean
* make read-topic: Reads from the otlp kafka topic
* make list-topics: Lists kafka topics and other information