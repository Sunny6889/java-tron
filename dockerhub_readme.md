# Java-Tron
Official Java implementation of [Tron protocol](https://tronprotocol.github.io/documentation-en/introduction/dpos/). 

Please refer to the [API Reference](https://tronprotocol.github.io/documentation-en/api/http/) for interacting with Tron FullNode.

# Quick start

Below are the steps for setting up a Tron FullNode that connects to the TRON Network.

## Prerequisites

### Hardware Requirements
Minimum:
- CPU with 8+ cores
- 16GB RAM
- 200GB free storage space to sync the Mainnet with Lite fullnode data snapshot
- 50 MBit/sec download Internet service

Recommended:
- Fast CPU with 16+ cores(32+ cores for a super representative)
- 32GB+ RAM(64GB+ for a super representative)
- High Performance SSD with at least 3TB free space for full data of Maninnet.
- 100+ MB/s download Internet service

### Docker

Please download and install the latest version of Docker from the official Docker website:
* Docker Installation for [Mac](https://docs.docker.com/docker-for-mac/install/)
* Docker Installation for [Windows](https://docs.docker.com/docker-for-windows/install/)

## Get docker image
There are two ways to obtain the Tron image:
- Pull it from the Tron Docker Hub.
- Build it from the java-tron source code.

### Using the official Docker images

Download the official Docker image from Docker Hub using the following command if you'd like to use the official images:
```
docker pull tronprotocol/java-tron
```
Check [Docker Hub](https://hub.docker.com/r/tronprotocol/java-tron/tags) for historical image.

### Build from source code

Building java-tron requires the git package. Clone the repository and switch to the master branch with the following commands:
```
git clone https://github.com/tronprotocol/java-tron.git
cd java-tron
git checkout -t origin/master
```

Then use the following command to navigate to the docker directory and start the build:
```
cd docker
docker build -t tronprotocol/java-tron .
```
Check the Dockerfile for build details. Essentially, Docker will pull the java-tron repository and build it using JDK 1.8.

## Run the container

You can run the following command to start java-tron:
```
docker run -it --name tron -d \
-p 8090:8090 -p 8091:8091 -p 18888:18888/udp -p 18888:18888/tcp -p 50051:50051 \
tronprotocol/java-tron 
```
The `-p` flag specifies the ports that the container needs to map to the host machine.

By default, it will use the [configuration](https://github.com/tronprotocol/java-tron/blob/develop/framework/src/main/resources/config.conf), 
which sets the fullNode to connect to the mainnet with genesis block settings in `genesis.block`.
Once the fullnode starts, it will begin to sync blocks with other peers starting from block number 1.

Check the logs using command `docker exec -it tron tail -f ./logs/tron.log`. It will show content similar to below. 
It shows the fullnode handshaking with peers successfully and then syncing for blocks. 
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
For abnormal cases, please check the troubleshooting section below.

### Run with customized configure
This image also supports customizing some startup parameters. Here is an example for running a FullNode as a witness with a customized configuration file:
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
The `-v` flag specifies the directory that the container needs to map to the host machine.
In the example above, the host file `/host/path/java-tron/conf/config-localtest.conf` will be used. For example, you can refer to the java-tron [config-localtest](https://github.com/tronprotocol/java-tron/blob/develop/framework/src/main/resources/config-localtest.conf). 

Inside the config file `node.p2p.version` is used to set the P2P network id. Only nodes with the same network id can shake hands successfully.
- TRON mainnet: node.p2p.version=11111
- Nile testnet: node.p2p.version = 201910292
- Private network：set to other values

Flags after `tronprotocol/java-tron` are used for java-tron start-up arguments:
- `-jvm` used for java virtual machine, the parameters must be enclosed in double quotes and braces. `"{-Xmx10g -Xms10g}"` sets the maximum and initial heap size to 10GB.
- `-c` defines the configuration file to use.
- `-d` defines the database file to use. You can mount a directory for `datadir` with snapshots. Please refer to [**Lite-FullNode**](https://tronprotocol.github.io/documentation-en/using_javatron/backup_restore/#_5). This can save time by syncing from a near-latest block number.
- `-w` means to start as a witness. You need to fill the `localwitness` field with private keys in configure file. Refer to the [guidance](https://tronprotocol.github.io/documentation-en/using_javatron/installing_javatron/#_3).

## Interact with FullNode 
After the fullnode runs successfully, you can interact with it using the HTTP API or wallet-cli. For more details, please refer to [guidance](https://tronprotocol.github.io/documentation-en/getting_started/getting_started_with_javatron/#interacting-with-java-tron-nodes-using-curl).

For example, a request to get block info with a specific number:
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
Notice: Before the local full node has synced with the latest block transactions, requests for account status or transaction information may be outdated or empty.

## Troubleshot 
After starting the docker container, use `docker exec -it tron tail -f ./logs/tron.log` to check if the full node is functioning as expected and to identify any errors when interacting with the full node.

If the following error cases do not cover your issue, please refer to [Issue Work Flow](https://tronprotocol.github.io/documentation-en/developers/issue-workflow/#issue-work-flow), then raise issue in [Github](https://github.com/tronprotocol/java-tron/issues).

### Error Case Handling

#### Zero Peer Connection 
If the logs show `Peer stats: all 0, active 0, passive 0`, it means Tron node cannot use **P2P Node Discovering Protocol** to find neighbors.
This protocol operates over UDP through port 18888. Therefore, the most likely cause of this issue is a network problem. 
Try debugging with the following steps:
- Use the command `docker ps` to check if the ports mapping includes `-p 18888:18888`.
- Verify your local network settings to ensure that port 18888 is not blocked.
- Restart the docker application(not the container) on your host, as discussed in this [issue](https://github.com/tronprotocol/java-tron/issues/6116#issuecomment-2541274062), which solved the problem.

# Contribution
Thank you for considering contributing to the source code! We appreciate contributions from everyone and are grateful for even the smallest fixes!
Please Check the [guidelines](https://tronprotocol.github.io/documentation-en/developers/java-tron/) here.

# License
The java-tron library is licensed under the GNU Lesser General Public License v3.0⁠, also included in our repository in the LICENSE file.
