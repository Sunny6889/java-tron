# Private Network

Here are the quick-start for setting up a Tron private network using Docker.

A private chain needs at least one fullnode running by [SR](https://tronprotocol.github.io/documentation-en/mechanism-algorithm/sr/) to produces blocks, and any number of FullNodes to synchronize blocks and broadcast transactions.

## Prerequisites

### Minimum Hardware Requirements
- CPU with 8+ cores
- 32GB RAM
- 100GB free storage space


### Docker

Please download and install the latest version of Docker from the official Docker website:
* Docker Installation for [Mac](https://docs.docker.com/docker-for-mac/install/)
* Docker Installation for [Windows](https://docs.docker.com/docker-for-windows/install/)


## Run the container

Create a Docker network used by containers to connect to each other:
```
export NETWORK_NAME="tron_private_network"
docker network create $NETWORK_NAME 
```
Then run docker containers with one witness and two normal fullnodes:
```
# Run a FullNode as witness
docker run -it --name tron_witness -d --network $NETWORK_NAME -p 8090:8090 -p 18888:18888 -p 18888:18888/udp -p 50051:50051 \
        -v /Users/sunnyjiao/Documents/java/java-tron/config:/java-tron/conf \
tronprotocol/java-tron \
        -jvm "{-Xmx10g}" \
        -c /java-tron/conf/private_net_config_witness.conf \
        -w

# Run a normal FullNode
docker run -it --name tron_node1 -d --network $NETWORK_NAME \
        -v /Users/sunnyjiao/Documents/java/java-tron/config:/java-tron/conf \
tronprotocol/java-tron \
        -jvm "{-Xmx10g}" \
        -c /java-tron/conf/private_net_config_nodes.conf


# Run another normal FullNode
docker run -it --name tron_node2 -d --network $NETWORK_NAME \
        -v /Users/sunnyjiao/Documents/java/java-tron/config:/java-tron/conf \
tronprotocol/java-tron \
        -jvm "{-Xmx10g}" \
        -c /java-tron/conf/private_net_config_nodes.conf
```




# Troubleshot
If you encounter any difficulties, please refer to the [Issue Work Flow](https://tronprotocol.github.io/documentation-en/developers/issue-workflow/#issue-work-flow), then raise an issue on [GitHub](https://github.com/tronprotocol/java-tron/issues).

# Advance
To set up a private network natively, refer to the [Deployment Guide](https://tronprotocol.github.io/documentation-en/using_javatron/private_network/). Make sure you set up all the configuration parameters mentioned above correctly.