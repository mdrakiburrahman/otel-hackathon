### Steps

`arc` namespace:
```powershell
# Create Kafka
microk8s kubectl apply -f C:\Users\mdrrahman\Documents\GitHub\otel-hackathon\Arc-otel-experiment\kubernetes-kafka-arc.yml
```

`rbc` namespace:

```powershell
microk8s kubectl create ns rbc
microk8s kubectl apply -f C:\Users\mdrrahman\Documents\GitHub\otel-hackathon\Arc-otel-experiment\kubernetes-kafka-elastic-rbc.yml
```

OTEL:
```powershell
# Certs are pulled from microk8s container because easier
# Configmap is created from microk8s container because easier

# Create OTEL Agent
microk8s kubectl apply -f C:\Users\mdrrahman\Documents\GitHub\otel-hackathon\Arc-otel-experiment\otel-combined\kubernetes-otel-agent.yml

# Create OTEL Collector
microk8s kubectl apply -f C:\Users\mdrrahman\Documents\GitHub\otel-hackathon\Arc-otel-experiment\otel-combined\kubernetes-otel-collector.yml
```