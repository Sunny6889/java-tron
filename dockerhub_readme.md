# Java-Tron
Official Java implementation of [Tron protocol](https://tronprotocol.github.io/documentation-en/introduction/dpos/). 

[API Reference](https://tronprotocol.github.io/documentation-en/api/http/) of Tron FullNode.

# Quick start

Below provides steps for setting up a Tron FullNode that connects to TRON Mainnet or Testnet.

## Prerequisites

### Docker

Please download and install the latest Docker from Docker official website:
* Docker Installation for [Mac](https://docs.docker.com/docker-for-mac/install/)
* Docker Installation for [Windows](https://docs.docker.com/docker-for-windows/install/)

## Get docker image
There are two ways to get Tron image:
- Pull from tron docker hub.
- Build from java-tron source code.

### Using the official Docker images

Download the official docker image from the Dockerhub with below command if you'd like to use the official images:
```
docker pull tronprotocol/java-tron
```

### Build from source code

Clone the java-tron repo from github and enter the directory `java-tron`:
```
git clone https://github.com/tronprotocol/java-tron.git
cd java-tron
```
Then Use the command below to navigate to the docker directory and start the build:
```
cd docker
docker build -t tronprotocol/java-tron .
```

## Run the container

You can run the command below to start the java-tron:
```
docker run -it --name tron -d \
-p 8090:8090 -p 8091:8091 -p 18888:18888/udp -p 18888:18888/tcp -p 50051:50051 \
--restart always tronprotocol/java-tron 
```
The `-p` flag defines the ports that the container needs to be mapped on the host machine.

By default, it will use [configure](https://github.com/tronprotocol/java-tron/blob/develop/framework/src/main/resources/config.conf), 
which set fullNode connect to mainnet with genesis block setting in `genesis.block`.
Once fullnode started, it will begin to sync blocks with other peers from block number 1.

Check the logs using command `docker exec -it tron tail -f ./logs/tron.log`, it will show content similar as below:
```
...
06:43:09.966 INFO  [DiscoverServer] [net](DiscoverServer.java:74) Discovery server started, bind port 18888
06:43:10.007 INFO  [main] [consensus](ConsensusService.java:84) consensus service start success
06:43:13.689 INFO  [connPool] [net](ConnPoolService.java:194) Connect to peer /54.82.161.39:18888
06:43:13.710 INFO  [connPool] [net](ConnPoolService.java:194) Connect to peer /52.53.189.99:18888
06:43:13.717 INFO  [connPool] [net](ConnPoolService.java:194) Connect to peer /54.153.94.160:18888
06:43:13.728 INFO  [connPool] [net](ConnPoolService.java:194) Connect to peer /43.198.142.160:18888
06:43:13.741 INFO  [connPool] [net](ConnPoolService.java:194) Connect to peer /54.179.207.68:18888
06:43:13.797 INFO  [peerClient-5] [net](Channel.java:140) Send message to channel /54.179.207.68:18888, [HelloMessage: from {
  address: "203.117.139.182"
  port: 18888
  nodeId: "p\rc\017o\225\255\035^?\023\252\346\262\216\033\310\260\204b\265\022\232\337\240d\216\233\232\361f\350\326\310\206\376h\220/\341R\200\234\233#9E\245\233\0241\253\300V\365 yo\350\360:\316\351C"
}
network_id: 11111
timestamp: 1734504193756
version: 1
... ...
06:43:13.800 INFO  [sync-handle-block] [consensus](DposService.java:162) Update solid block number to 678
06:43:13.800 INFO  [sync-handle-block] [DB](DynamicPropertiesStore.java:2193) Update latest block header id = 00000000000002b842047d63fa2700b420051b9a770e2c10cbde031b76c5980f.
06:43:13.800 INFO  [sync-handle-block] [DB](DynamicPropertiesStore.java:2185) Update latest block header number = 696.
06:43:13.800 INFO  [sync-handle-block] [DB](DynamicPropertiesStore.java:2177) Update latest block header timestamp = 1529893626000.
... ...
```
For abnormal cases please check below troubleshot section.

### Run with customized configure
This image also supports customizing some startup parameters, here is an example for running a FullNode as witness with customized configuration file:
```
docker run -it --name tron -d -p 8090:8090 -p 8091:8091 -p 18888:18888 -p 50051:50051 \
           -v /host/path/java-tron/conf:/java-tron/conf \ 
           -v /host/path/java-tron/datadir:/java-tron/data \ 
           tronprotocol/java-tron \
           -jvm "{-Xmx10g -Xms10g}" \
           -c /java-tron/conf/config-localtest.conf \
           -d /java-tron/data \
           -w
```
The `-v` flag defines the directory that the container needs to be mapped on the host machine. 
In above example the host file `/host/path/java-tron/conf/config-localtest.conf` will be used, for example in java-tron [config-localtest](https://github.com/tronprotocol/java-tron/blob/develop/framework/src/main/resources/config-localtest.conf). 

Inside the config file `node.p2p.version` is used to set the P2P network id. Only nodes with the same network id can shake hands successfully.
- TRON mainnet: node.p2p.version=11111
- Nile testnet: node.p2p.version = 201910292
- Private network：set to other values

Flags after `tronprotocol/java-tron` are used for java-tron start-up arguments:
- `-c` defines the configuration file to use.
- `-d` defines the database file to use. You could mount directory for datadir with snapshots, please refer to [guidance](https://tronprotocol.github.io/documentation-en/using_javatron/backup_restore/#_5).
  It could save time to sync from near latest block number。 
- `-w` means start as witness, you need fill `localwitness` with private key in configure file, refer to [guidance](https://tronprotocol.github.io/documentation-zh/using_javatron/installing_javatron/#_3).

Note: The jvm parameters must be enclosed in double quotes and braces.

## Interact with FullNode 
After the local fullnode image run successfully, you could play with it using http API or wallet-cli, refer the [guidance](https://tronprotocol.github.io/documentation-en/getting_started/getting_started_with_javatron/#interacting-with-java-tron-nodes-using-curl).

For example request to get block info with num:
```
curl --location 'localhost:8090/wallet/getblock' \
--header 'Content-Type: application/json' \
--data '{
    "id_or_num": "100",
    "detail": true
}'
```
Response:
```
{
    "blockID": "00000000000000644df09e6883a3a7900814f8d78cf47b255b7ed284527a773d",
    "block_header": {
        "raw_data": {
            "number": 100,
            "txTrieRoot": "0000000000000000000000000000000000000000000000000000000000000000",
            "witness_address": "414b4778beebb48abe0bc1df42e92e0fe64d0c8685",
            "parentHash": "0000000000000063ed8544c4c17fc053dfc729e610673c783bcdc3cf0781b07f",
            "timestamp": 1529891811000
        },
        "witness_signature": "277d4440e2feb552b6d2d557ba407f68310887020fcc7ef6e2733286a0d13c703ebf2306293bda9d2ddac09835be67583c736a65494115825b6f4ab6a15f1e0f01"
    }
}
```
Notice: Before the local fullnode synced with the latest block transactions, request for account status or transaction infos maybe outdated or empty.

## Troubleshot 
After you start the docker container, check `docker exec -it tron tail -f ./logs/tron.log` to see whether fullnode works as expected, or there is error when you interact with the fullnode.

### Error Case Handling
#### Zero Peer Connection 
If the logs show `Peer stats: all 0, active 0, passive 0`, restart the docker app(not the image) in your host.

