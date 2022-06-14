# Setup

## Environment: Arc Indirect on AKS

> **Pre-req**: Have an Arc Data Indirect/Direct cluster

### Connect to devcontainer
```powershell
code -r .\Arc-otel-experiment\
```

### Get kubeconfig from Azure

```bash
export resourceGroup='arcrbac-46714061-rg'
export clusterName='arcrbac-46714061-aks'
export ns='azure-arc-data'

# Login as service principal
az login --service-principal --username $spnClientId --password $spnClientSecret --tenant $spnTenantId
az account set --subscription $subscriptionId

# Getting AKS cluster credentials kubeconfig file
az aks get-credentials --resource-group $resourceGroup --name $clusterName --admin

kubectl get nodes
```

### Grab Arc secrets necessary for OTEL work

```bash
# Get Secrets
kubectl get secret controller-db-rw-secret -n $ns -o json | jq -r '.data.password' | base64 -d
kubectl get secret controller-db-rw-secret -n $ns -o json | jq -r '.data.username' | base64 -d
kubectl get secret controller-db-data-encryption-key-secret -n $ns -o json | jq -r '.data.encryptionPassword' | base64 -d

# Expose as a LoadBalancer
cat <<EOF | kubectl create -f -
apiVersion: v1
kind: Service
metadata:
  name: controldb-external-svc
  namespace: $ns
spec:
  type: LoadBalancer
  selector:
    ARC_NAMESPACE: $ns
    app: controldb
    plane: control
    role: controldb
  ports:
  - name: database-port
    port: 31433
    protocol: TCP
    targetPort: 1433
EOF

kubectl get svc controldb-external-svc -n $ns
```

Summary:
| Tech | Endpoint              | Credentials                                        | Encryption Password              |
| ---- | --------------------- | -------------------------------------------------- | -------------------------------- |
| FSM  | `20.241.209.220,31433`  | controldb-rw-user:LIFR2iH3sudqBxvoWc-4O1wEqMDGnZgC | e9VYRNwwnO-_kOlOgFJOOyqV6AUiD5MZ |

Grab relevant Arc certs out and create configMap for OTEL:

```bash
chmod +x /workspaces/otel-hackathon/Arc-otel-experiment/scripts/pull-certs-into-configMap.sh
/workspaces/otel-hackathon/Arc-otel-experiment/scripts/pull-certs-into-configMap.sh
# tar: Removing leading `/' from member names
# configmap/otel-cluster-config created
```

### Deploy SQL MI
```bash
kubectl apply -f /workspaces/otel-hackathon/Arc-otel-experiment/kubernetes-sqlmi.yaml
# secret/guedlcmi1p001-login-secret created
# sqlmanagedinstance.sql.arcdata.microsoft.com/guedlcmi1p001 created
```

### Dotnet Container: Inject OTEL config into Controller File Delivery Table

Open up VSCode in seperate window containing the dotnet injector

```powershell
cd C:\Users\mdrrahman\Documents\GitHub\otel-hackathon
code -n Arc-dotnet-file-delivery-injector
```
> Make sure to localize to IP of Controller DB and Password and encryptionKey in `devcontainer.env`

```bash
cd Injector
dotnet clean
dotnet build
dotnet run
```

The script updates the following entry in the FSM - corresponding to the single fluentbit container's conf file that controls fluentbit behavior in the MI:

```sql
USE Controller;
OPEN SYMMETRIC KEY ControllerDbSymmetricKey DECRYPTION BY PASSWORD = 'HnUnfSCkkCtMeRZbN3AQvgIDyTVNBSVl';

SELECT file_path, secret_decrypted FROM (
    select *, convert(varchar(max), DECRYPTBYKEY(data)) as 'secret_decrypted' from controller.dbo.Files
) AS T
WHERE file_path = '/config/namespaces/azure-arc-data/scaledsets/gpm0mi01/containers/fluentbit/files/fluentbit-out-elasticsearch.conf'
ORDER BY created_time DESC
```
We will need to reboot the container so it picks up this new file from FSM during bootup - but we will do this **after** deploying the OTEL collector to ensure Fluentbit doesn't error.

### Kafka and Elastic setup

`laas` namespace:
```bash
kubectl create ns laas
kubectl apply -f /workspaces/otel-hackathon/Arc-otel-experiment/kubernetes-kafka-elastic-laas.yml
# deployment.apps/elk created
# service/elk-service created
# deployment.apps/zookeeper-1 created
# deployment.apps/kafka-1 created
# deployment.apps/kafdrop-1 created
# service/kafdrop-ui created
# service/zookeeper-1 created
# service/kafka-service created

# Hack for KStream demo
kubectl get svc -n laas | grep kafka-service
# kafka-service   LoadBalancer   192.168.92.239   52.226.243.196   9092:31440/TCP                  7m49s

# Go back into kubernetes-kafka-elastic-laas.yml and in the Kafka Deployment, set:
# env:
#   - name: ADVERTISED_HOST_NAME
#   value: 52.226.243.196 # <- This is a hack to get kafka advertised hostnames to work externally without DNS

# Reapply
kubectl apply -f /workspaces/otel-hackathon/Arc-otel-experiment/kubernetes-kafka-elastic-laas.yml
# ..
# deployment.apps/kafka-1 configured
# ...
```

### OTEL setup

```bash
# Create OTEL Agent - pulls metrics from nodes
kubectl apply -f /workspaces/otel-hackathon/Arc-otel-experiment/kubernetes-otel-agent.yml
# configmap/otel-agent-conf created
# daemonset.apps/otel-agent created

# Create OTEL Collector - receives stuff from fluentbit
kubectl apply -f /workspaces/otel-hackathon/Arc-otel-experiment/kubernetes-otel-collector.yml
```
Reboot SQL MI to fire Fluentbit up with the new config:
```bash
kubectl delete pod guedlcmi1p001-0 -n azure-arc-data --grace-period=0 --force
```
Tail fluentbit container in case something breaks:
```bash
# The file we pushed via FSM
cat /run/fluentbit/fluentbit-out-elasticsearch.conf
# ###############################
# # Elasticsearch - original
# ###############################
# [OUTPUT]
#     Name             es
#     Match            *
#     Host             ${FLUENT_ELASTICSEARCH_HOST}
#     Port             ${FLUENT_ELASTICSEARCH_PORT}
#     Logstash_Format  On
#     Retry_Limit      False
#     tls              On
#     tls.verify       On
#     tls.ca_file      /var/run/configmaps/cluster/cluster-ca-certificate.crt
#     tls.crt_file     /var/run/secrets/managed/certificates/fluentbit/fluentbit-certificate.pem
#     tls.key_file     /var/run/secrets/managed/certificates/fluentbit/fluentbit-privatekey.pem

# ###############################
# # OTEL - laas
# ###############################
# [OUTPUT]
#     Name                  Forward
#     Match                 *
#     Host                  otel-collector.azure-arc-data.svc.cluster.local
#     Port                  8006
#     Require_ack_response  True
#     tls                   Off

# Fluentbit logs
tail -f /var/log/fluentbit/fluentbit.log -n +1
```

Go into `laas` Kafdrop - make sure the 2 otel topics are showing.
Go into Elastic UI and create index against otel.

### Java Container: KStream Processing
```powershell
cd C:\Users\mdrrahman\Documents\GitHub\otel-hackathon
code -r Arc-java-kstream-laas
```
Inside the container:
```bash
# Set environment variables in devcontainer.env, specially the LaaS Kafka Broker Public IP Address

# Build and run package
clear && mvn clean install && java -jar target/kstream-arc-1.0-SNAPSHOT.jar
```

### K8s Dashboard
Open up Kubernetes Dashboard in this container:
```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.5.0/aio/deploy/recommended.yaml

kubectl proxy
# http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy
# Point to kubeconfig: C:\Users\mdrrahman\.kube
```