package org.tron.core.db;

import org.checkerframework.checker.units.qual.s;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.FileUtil;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.store.AccountStore;
import org.tron.core.store.ContractStore;
import org.tron.core.store.DelegatedResourceStore;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.capsule.AccountCapsule;
import org.tron.core.capsule.ContractCapsule;
import org.tron.core.capsule.DelegatedResourceCapsule;
import org.tron.protos.Protocol.AccountType;
import org.tron.protos.contract.SmartContractOuterClass.SmartContract;
import com.google.protobuf.ByteString;
import org.tron.common.utils.ByteArray;
import org.tron.protos.contract.BalanceContract.TransferContract;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;
import javax.annotation.Resource;
import org.tron.core.db2.ISession;
import org.tron.core.db2.core.SnapshotManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.security.SecureRandom;

/**
 * 测试LevelDB在累积非固化块时对get操作性能的影响
 */
public class LevelDBPerformanceTest {

    private static final Logger log = LoggerFactory.getLogger(LevelDBPerformanceTest.class);

    private static final String dbPath = "output_leveldb_performance_test";
    private TronApplicationContext context;
    private Manager dbManager;
    private AccountStore accountStore;
    private ContractStore contractStore;
    private DelegatedResourceStore delegatedResourceStore;

    @Resource
    private TransactionStore transactionStore;
    private Random random = new SecureRandom();

    @Before
    public void init() {
        Args.setParam(new String[] { "--output-directory", dbPath }, "config.conf");
        context = new TronApplicationContext(DefaultConfig.class);
        dbManager = context.getBean(Manager.class);
        accountStore = dbManager.getAccountStore();
        contractStore = dbManager.getContractStore();
        delegatedResourceStore = dbManager.getDelegatedResourceStore();
        transactionStore = dbManager.getTransactionStore();

        // Reset all stores
        accountStore.reset();
        contractStore.reset();
        delegatedResourceStore.reset();
        transactionStore.reset();
    }

    @After
    public void destroy() {
        Args.clearParam();
        context.destroy();
        FileUtil.deleteDir(new File(dbPath));
    }

    /**
     * WitnessStore 性能测试：测试不同快照数量对get操作性能的影响
     */
    @Test
    public void testAccountStoreSnapshotPerformance() {
        // 数组大小为非固化块数的一倍，因为下面会设置 revokingStore.setMaxSize((int) (snapshotCount * 0.5));
        // 把一半的数据 flush 到 disk，模拟真实的情况有数据落入磁盘
        int[] snapshotCounts = { 38, 1000, 2000, 4000, 6000 };

        for (int snapshotCount : snapshotCounts) {
            List<byte[]> testAddresses = new ArrayList<>();

            SnapshotManager revokingStore = context.getBean(SnapshotManager.class);

            // 设置最大快照数量，防止自动flush到磁盘
            revokingStore.setMaxSize((int) (snapshotCount * 0.5));

            // 创建指定数量的快照
            for (int i = 0; i < snapshotCount; i++) {
                try (ISession tmpSession = revokingStore.buildSession()) {
                    // 在每个快照中插入 400 个复杂账户
                    for (int j = 0; j < 400; j++) {
                        byte[] tempAddress = generateRandomAddress();

                        AccountCapsule tempAccount = new AccountCapsule(
                                ByteString.copyFrom(tempAddress),
                                ByteString.copyFromUtf8("Account" + i + "_" + j),
                                AccountType.Normal);
                        testAddresses.add(tempAddress);
                        tempAccount.setBalance(1000000L + i * 1000 + j);
                        tempAccount.setFrozen(500000L + i * 500 + j, System.currentTimeMillis() + 86400000L);
                        tempAccount.setNetUsage(100L + i);
                        tempAccount.setEnergyUsage(200L + i);
                        tempAccount.setLatestConsumeTime(System.currentTimeMillis() - i * 1000);
                        tempAccount.setLatestOperationTime(System.currentTimeMillis());

                        accountStore.put(tempAddress, tempAccount);
                    }
                    tmpSession.commit();
                }
            }

            // 测试get操作性能 - 随机读取原始测试账户
            long startTime = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                byte[] randomAddress = testAddresses.get(random.nextInt(testAddresses.size()));
                AccountCapsule account = accountStore.get(randomAddress);
                if (account == null) {
                    log.warn("Account not found for address: " + java.util.Arrays.toString(randomAddress));
                }
            }
            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1000000; // 转换为毫秒
            double avgDuration = duration / 10000.0;

            log.info(String.format("AccountStore测试 - 快照数量: %d, 10000次get操作总耗时: %dms, 平均耗时: %.3fms",
                    snapshotCount, duration, avgDuration));

            // 清理快照
            while (revokingStore.size() > 0) {
                try {
                    revokingStore.pop();
                } catch (Exception e) {
                    // 忽略清理异常
                    break;
                }
            }
        }
    }

    /**
     * TransactionStore 快照性能测试
     */
    @Test
    public void testTransactionStoreSnapshotPerformance() {
        // 数组大小为非固化块数的一倍，因为下面会设置 revokingStore.setMaxSize((int) (snapshotCount * 0.5));
        // 把一半的数据flush 到 disk，模拟真实的情况有数据落入磁盘
        int[] snapshotCounts = { 38, 1000, 2000, 4000, 6000 };

        SnapshotManager revokingStore = context.getBean(SnapshotManager.class);

        List<ByteString> transactionIds = new ArrayList<>();

        int traxNum = 0;
        // 先再磁盘填充相当于1000个区块的数据
        for (int i = 0; i < 1000; i++) {
            try (ISession tmpSession = revokingStore.buildSession()) {
                // 在每个快照中插入 400 个复杂交易
                for (int j = 0; j < 400; j++) {
                    TransactionCapsule tempTransaction = createComplexTransaction(traxNum);
                    transactionIds.add(ByteString.copyFrom(tempTransaction.getTransactionId().getBytes()));
                    transactionStore.put(tempTransaction.getTransactionId().getBytes(), tempTransaction);
                    traxNum++;
                }
                tmpSession.commit();
            }
        }

        for (int snapshotCount : snapshotCounts) {
            log.info("开始测试 TransactionStore {} 个快照的性能", snapshotCount);

            // 设置最大快照数量，防止自动flush到磁盘
            revokingStore.setMaxSize((int) (snapshotCount * 0.5));

            // 创建指定的非固化块数
            for (int i = 0; i < snapshotCount; i++) {
                try (ISession tmpSession = revokingStore.buildSession()) {
                    // 在每个快照中插入 400 个复杂交易
                    for (int j = 0; j < 400; j++) {
                        TransactionCapsule tempTransaction = createComplexTransaction(traxNum);
                        transactionIds.add(ByteString.copyFrom(tempTransaction.getTransactionId().getBytes()));
                        transactionStore.put(tempTransaction.getTransactionId().getBytes(), tempTransaction);
                        traxNum++;
                    }
                    tmpSession.commit();
                }
            }

            // 测试随机get操作的性能
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                try {
                    int randomIndex = random.nextInt(traxNum);
                    ByteString transactionId = transactionIds.get(randomIndex);
                    transactionStore.get(transactionId.toByteArray());
                } catch (Exception e) {
                    // 忽略读取异常，继续测试
                }
            }
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            double avgTime = totalTime / 1000.0;

            log.info("TransactionStore {} 个快照，10000次get操作总耗时: {}ms，平均耗时: {}ms",
                    snapshotCount, totalTime, avgTime);
        }

    }

    /**
     * 创建复杂的交易，包含多种类型的合约
     */
    private TransactionCapsule createComplexTransaction(int index) {
        // 创建转账合约
        TransferContract.Builder transferBuilder = TransferContract.newBuilder()
                .setOwnerAddress(ByteString.copyFrom(generateRandomAddress()))
                .setToAddress(ByteString.copyFrom(generateRandomAddress()))
                .setAmount(1000000 + index);

        // 创建复杂的交易，包含多个合约
        TransactionCapsule transaction = new TransactionCapsule(transferBuilder.build(), ContractType.TransferContract);

        // 设置交易的时间戳和过期时间
        transaction.setTimestamp(System.currentTimeMillis());
        transaction.setExpiration(System.currentTimeMillis() + 60000); // 1分钟后过期

        // 设置费用限制
        transaction.setFeeLimit(1000000);

        return transaction;
    }

    /**
     * 生成随机地址
     */
    private byte[] generateRandomAddress() {
        byte[] address = new byte[21];
        random.nextBytes(address);
        // 确保地址以0x41开头（TRON地址格式）
        address[0] = 0x41;
        return address;
    }

    /**
     * ContractStore 快照性能测试
     */
    @Test
    public void testContractStoreSnapshotPerformance() {
        // 数组大小为非固化块数的一倍，因为下面会设置 revokingStore.setMaxSize((int) (snapshotCount * 0.5));
        // 把一半的数据 flush 到 disk，模拟真实的情况有数据落入磁盘
        int[] snapshotCounts = { 38, 1000, 2000, 4000, 6000 };

        for (int snapshotCount : snapshotCounts) {
            log.info("开始测试 ContractStore {} 个快照的性能", snapshotCount);

            List<byte[]> contractAddresses = new ArrayList<>();
            SnapshotManager revokingStore = context.getBean(SnapshotManager.class);

            // 设置最大快照数量，防止自动flush到磁盘
            revokingStore.setMaxSize((int) (snapshotCount * 0.5));

            // 创建指定数量的快照
            for (int i = 0; i < snapshotCount; i++) {
                try (ISession tmpSession = revokingStore.buildSession()) {
                    // 在每个快照中插入 400 个合约
                    for (int j = 0; j < 400; j++) {
                        byte[] contractAddress = ByteArray.fromHexString("41" + String.format("%040d", i * 400 + j));
                        byte[] originAddress = ByteArray.fromHexString("42" + String.format("%040d", i * 400 + j));

                        SmartContract smartContract = SmartContract.newBuilder()
                                .setOriginAddress(ByteString.copyFrom(originAddress))
                                .setContractAddress(ByteString.copyFrom(contractAddress))
                                .setBytecode(ByteString.copyFromUtf8("test bytecode " + (i * 400 + j)))
                                .setConsumeUserResourcePercent(100)
                                .setOriginEnergyLimit(1000000)
                                .build();

                        ContractCapsule tempContract = new ContractCapsule(smartContract);
                        contractAddresses.add(contractAddress);
                        contractStore.put(contractAddress, tempContract);
                    }
                    tmpSession.commit();
                }
            }

            // 测试随机get操作的性能
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                try {
                    int randomIndex = random.nextInt(contractAddresses.size());
                    byte[] contractAddress = contractAddresses.get(randomIndex);
                    contractStore.get(contractAddress);
                } catch (Exception e) {
                    // 忽略读取异常，继续测试
                }
            }
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            double avgTime = totalTime / 1000.0;

            log.info("ContractStore {} 个快照，10000次get操作总耗时: {}ms，平均耗时: {}ms",
                    snapshotCount, totalTime, avgTime);

            // 清理快照
            while (revokingStore.size() > 0) {
                try {
                    revokingStore.pop();
                } catch (Exception e) {
                    // 忽略清理异常
                    break;
                }
            }

            // 重置存储
            contractStore.reset();
        }
    }

    /**
     * DelegatedResourceStore 快照性能测试
     */
    @Test
    public void testDelegatedResourceStoreSnapshotPerformance() {
        // 数组大小为非固化块数的一倍，因为下面会设置 revokingStore.setMaxSize((int) (snapshotCount * 0.5));
        // 把一半的数据 flush 到 disk，模拟真实的情况有数据落入磁盘
        int[] snapshotCounts = { 38, 1000, 2000, 4000, 6000 };

        for (int snapshotCount : snapshotCounts) {
            log.info("开始测试 DelegatedResourceStore {} 个快照的性能", snapshotCount);

            List<byte[]> delegatedResourceKeys = new ArrayList<>();
            SnapshotManager revokingStore = context.getBean(SnapshotManager.class);

            // 设置最大快照数量，防止自动flush到磁盘
            revokingStore.setMaxSize((int) (snapshotCount * 0.5));

            // 创建指定数量的快照
            for (int i = 0; i < snapshotCount; i++) {
                try (ISession tmpSession = revokingStore.buildSession()) {
                    // 在每个快照中插入 400 个委托资源
                    for (int j = 0; j < 400; j++) {
                        byte[] fromAddress = ByteArray.fromHexString("41" + String.format("%040d", i * 400 + j));
                        byte[] toAddress = ByteArray.fromHexString("42" + String.format("%040d", i * 400 + j));

                        DelegatedResourceCapsule tempDelegatedResource = new DelegatedResourceCapsule(
                                ByteString.copyFrom(fromAddress), ByteString.copyFrom(toAddress));
                        tempDelegatedResource.setFrozenBalanceForEnergy(1000000L + (i * 400 + j),
                                System.currentTimeMillis() + 86400000L);
                        tempDelegatedResource.setFrozenBalanceForBandwidth(2000000L + (i * 400 + j),
                                System.currentTimeMillis() + 86400000L);

                        byte[] key = DelegatedResourceCapsule.createDbKey(fromAddress, toAddress);
                        delegatedResourceKeys.add(key);
                        delegatedResourceStore.put(key, tempDelegatedResource);
                    }
                    tmpSession.commit();
                }
            }

            // 测试随机get操作的性能
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                try {
                    int randomIndex = random.nextInt(delegatedResourceKeys.size());
                    byte[] key = delegatedResourceKeys.get(randomIndex);
                    delegatedResourceStore.get(key);
                } catch (Exception e) {
                    // 忽略读取异常，继续测试
                }
            }
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            double avgTime = totalTime / 1000.0;

            log.info("DelegatedResourceStore {} 个快照，10000次get操作总耗时: {}ms，平均耗时: {}ms",
                    snapshotCount, totalTime, avgTime);

            // 清理快照
            while (revokingStore.size() > 0) {
                try {
                    revokingStore.pop();
                } catch (Exception e) {
                    // 忽略清理异常
                    break;
                }
            }

            // 重置存储
            delegatedResourceStore.reset();
        }
    }

}