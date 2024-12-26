#!/bin/bash

NETWORK_NAME="tron_private_network"
#export NETWORK_NAME="tron_private_network"

# Create the network if it does not exist.
if ! docker network ls | grep -q $NETWORK_NAME; then
    docker network create $NETWORK_NAME
fi

# Build a private Tron network with witnesses and other fullnode by following the commands below:
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


# Check logs of container tron_witness
docker exec -it tron_witness tail -f ./logs/tron.log