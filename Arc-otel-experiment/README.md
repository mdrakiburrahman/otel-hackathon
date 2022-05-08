# Setup

## AKS

> **Pre-req**: Have an Arc Data Indirect/Direct cluster that has an MI deployed

### Artifact setup
Get kubeconfig from Azure:
```bash
# Login as service principal
az login --service-principal --username $spnClientId --password $spnClientSecret --tenant $spnTenantId
az account set --subscription $subscriptionId

# Getting AKS cluster credentials kubeconfig file
az aks get-credentials --resource-group $resourceGroup --name $clusterName --admin

kubectl get nodes

export ns=arc-primary
```

Grab secrets necessary for OTEL work:
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
```
Summary:
| Tech | Endpoint              | Credentials                                        | Encryption Password              |
| ---- | --------------------- | -------------------------------------------------- | -------------------------------- |
| FSM  | `20.120.66.217,31433` | controldb-rw-user:7l1ZKJpTqbFXUzmJf1w-mgGk3-8D1DW- | HnUnfSCkkCtMeRZbN3AQvgIDyTVNBSVl |

Grab relevant Arc certs out and create configMap for OTEL:

```bash
chmod +x /workspaces/otel-hackathon/Arc-otel-experiment/scripts/pull-certs-into-configMap.sh
/workspaces/otel-hackathon/Arc-otel-experiment/scripts/pull-certs-into-configMap.sh
# tar: Removing leading `/' from member names
# configmap/otel-cluster-config created
```

Inject OTEL config into Controller File Delivery Table:

Open up VSCode in seperate window containing the dotnet injector

```powershell
cd C:\Users\mdrrahman\Documents\GitHub\otel-hackathon
code -r Arc-file-delivery-injector
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
WHERE file_path = '/config/namespaces/arc-primary/scaledsets/gpm0mi01/containers/fluentbit/files/fluentbit-out-elasticsearch.conf'
ORDER BY created_time DESC
```
We will need to reboot the container so it picks up this new file from FSM during bootup.

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
```

### OTEL setup

```bash
# Create OTEL Agent - pulls metrics from nodes
kubectl apply -f /workspaces/otel-hackathon/Arc-otel-experiment/kubernetes-otel-agent.yml

# Create OTEL Collector - receives stuff from fluentbit
kubectl apply -f /workspaces/otel-hackathon/Arc-otel-experiment/kubernetes-otel-collector.yml
```
Reboot SQL MI to fire Fluentbit up:
```bash
kubectl delete pod gpm0mi01-0 -n arc-primary --grace-period=0 --force
```
Tail fluentbit in case something breaks.

