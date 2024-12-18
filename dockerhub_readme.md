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
-p 8092:8092 -p 8091:8091 -p 18888:18888 -p 50051:50051 \
--restart always tronprotocol/java-tron 
```
The `-p` flag defines the ports that the container needs to be mapped on the host machine.
By default, it will use [configuration](https://github.com/tronprotocol/java-tron/blob/develop/framework/src/main/resources/config.conf).
This fullNode will connect to mainnet with genesis block setting in [configuration](https://github.com/tronprotocol/java-tron/blob/develop/framework/src/main/resources/config.conf#L397).
Once fullnode started it will begin to sync blocks with other peers from block number 1.

Check the logs using command `docker exec -it tron tail -f ./logs/tron.log`

This image also supports customizing some startup parameters，here is an example for running a FullNode as a witness use customized configuration file:
```
docker run -it --name tron -d -p 8080:8080 -p 8090:8090 -p 18888:18888 -p 50051:50051 \
           -v /host/path/java-tron/config:/java-tron/conf \ 
           -v /host/path/java-tron/output-directory:/java-tron/data \ 
           tronprotocol/java-tron \
           -jvm "{-Xmx10g -Xms10g}" \
           -c /java-tron/conf/config-localtest.conf \
           -d /java-tron/data \
           -w # witness
```

The `-v` flag defines the ports that the container needs to be mapped on the host machine.
Note: The to mount directory for conf must contain the file `config-localtest.conf`([link](https://github.com/tronprotocol/java-tron/blob/develop/framework/src/main/resources/config-localtest.conf)) referred by below `-c` command. The jvm parameters must be enclosed in double quotes and braces.
You could mount directory for datadir with snapshots, please refer to [guidance](https://tronprotocol.github.io/documentation-zh/using_javatron/backup_restore/#_5). 
It could save time to sync from latest block number。
`-w` means start as witness, you need fill `localwitness` with private key in the above conf file, refer to [guidance](https://tronprotocol.github.io/documentation-zh/using_javatron/installing_javatron/#_3).

## Quickstart for using docker-tron-quickstart
The purpose of it is to set up a complete private network for Tron development. Through TRON Quickstart, users can deploy DApps, smart contracts, and interact with the TronWeb library.
Check more information at [Quickstart:](https://github.com/TRON-US/docker-tron-quickstart)

The docker image exposes:
- Full Node
- Solidity Node
- Event Server.

### Node.JS Console
Node.JS is used to interact with the Full and Solidity Nodes via Tron-Web.  
[Node.JS](https://nodejs.org/en/) Console Download

### Clone TRON Quickstart
```shell
git clone https://github.com/TRON-US/docker-tron-quickstart.git
```  

### Pull the image using docker:
```shell
docker pull trontools/quickstart
```  

## Docker Commands
Here are some useful docker commands, which will help you manage the TRON Quickstart Docker container on your machine.

**To list all active containers on your machine, run:**
```shell
docker container ps
```  
**Output:**
```shell
docker container ps

CONTAINER ID        IMAGE               COMMAND                 CREATED             STATUS              PORTS                                              NAMES
513078dc7816        tron                "./quickstart v2.0.0"   About an hour ago   Up About an hour    0.0.0.0:9090->9090/tcp, 0.0.0.0:18190->18190/tcp   tron
```  
**To kill an active container, run:**
```shell
docker container kill 513078dc7816   // use your container ID
```  

### How to check the logs of the FullNode ###
```
  docker exec -it tron tail -f /tron/logs/tron.log 
```

 <details>

<summary>Output: something like following </summary>

  ```
  number=204
  parentId=00000000000000cb0985978b3c780e4219dc51e4329beecabe7b71f99d269985
  witness address=41928c9af0651632157ef27a2cf17ca72c575a4d21
  generated by myself=true
  generate time=2019-12-09 18:33:33.0
  txs are empty
  ]
  18:33:33.008 INFO  [Thread-5] [DB](Manager.java:1095) pushBlock block number:204, cost/txs:1/0
  18:33:33.008 INFO  [Thread-5] [witness](WitnessService.java:283) Produce block successfully, blockNumber:204, abSlot[525305471], blockId:00000000000000ccc37f1f5c2ceb574d14c490e3d0b86909855646f9384ba666, transactionSize:0, blockTime:2019-12-09T18:33:33.000Z, parentBlockId:00000000000000cb0985978b3c780e4219dc51e4329beecabe7b71f99d269985
  18:33:33.008 INFO  [Thread-5] [net](AdvService.java:156) Ready to broadcast block Num:204,ID:00000000000000ccc37f1f5c2ceb574d14c490e3d0b86909855646f9384ba666
  ........  etc
  ```
</details>
