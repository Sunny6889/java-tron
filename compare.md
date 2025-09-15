
```log
Affect(class count: 1 , method count: 1) cost in 357 ms, listenerId: 1
`---ts=2025-08-28 16:38:31.933;thread_name=sync-handle-block;id=90;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@1b6d3586
    `---[2027.683405ms] org.tron.core.db.Manager:processBlock()
        +---[0.13% 2.675653ms ] org.tron.consensus.Consensus:validBlock() #1780
        +---[0.00% 0.009971ms ] org.tron.core.ChainBaseManager:getBalanceTraceStore() #1784
        +---[0.00% 0.01302ms ] org.tron.core.store.BalanceTraceStore:initCurrentBlockBalanceTrace() #1784
        +---[0.00% 0.00789ms ] org.tron.core.ChainBaseManager:getDynamicPropertiesStore() #1787
        +---[0.00% 0.014941ms ] org.tron.core.store.DynamicPropertiesStore:saveBlockEnergyUsage() #1787
        +---[7.74% 156.981286ms ] org.tron.core.db.Manager:preValidateTransactionSign() #1791
        +---[0.00% 0.027711ms ] org.tron.core.capsule.TransactionRetCapsule:<init>() #1798
        +---[0.00% 0.034641ms ] org.tron.common.zksnark.MerkleContainer:resetCurrentMerkleTree() #1801
        +---[0.10% 1.967261ms ] org.tron.core.db.accountstate.callback.AccountStateCallBack:preExecute() #1802
        +---[0.00% 0.00883ms ] org.tron.core.capsule.BlockCapsule:getNum() #1804
        +---[0.00% 0.007081ms ] org.tron.core.capsule.BlockCapsule:getTransactions() #1805
        +---[0.03% min=9.7E-4ms,max=0.01972ms,total=0.543239ms,count=354] org.tron.core.ChainBaseManager:getDynamicPropertiesStore() #1806
        +---[0.07% min=0.00203ms,max=0.116544ms,total=1.362942ms,count=354] org.tron.core.store.DynamicPropertiesStore:allowConsensusLogicOptimization() #1806
        +---[0.04% min=9.7E-4ms,max=0.075972ms,total=0.743944ms,count=354] org.tron.core.capsule.TransactionCapsule:retCountIsGreatThanContractCount() #1807
        +---[0.05% min=8.8E-4ms,max=0.395942ms,total=0.930081ms,count=354] org.tron.core.capsule.TransactionCapsule:setBlockNum() #1812
        +---[0.02% min=8.6E-4ms,max=0.033861ms,total=0.506199ms,count=354] org.tron.core.db.accountstate.callback.AccountStateCallBack:preExeTrans() #1816
        +---[85.03% min=0.887558ms,max=19.60171ms,total=1724.052521ms,count=354] org.tron.core.db.Manager:processTransaction() #1817
        +---[0.04% min=0.0011ms,max=0.015651ms,total=0.720449ms,count=354] org.tron.core.db.accountstate.callback.AccountStateCallBack:exeTransFinish() #1818
        +---[0.00% 0.024701ms ] org.tron.core.capsule.TransactionRetCapsule:addAllTransactionInfos() #1823
        +---[0.00% 0.00485ms ] org.tron.core.db.accountstate.callback.AccountStateCallBack:executePushFinish() #1824
        +---[0.00% 0.00308ms ] org.tron.core.db.accountstate.callback.AccountStateCallBack:exceptionFinish() #1826
        +---[0.00% 0.00246ms ] org.tron.core.capsule.BlockCapsule:getNum() #1828
        +---[0.23% 4.587973ms ] org.tron.common.zksnark.MerkleContainer:saveCurrentMerkleTreeAsBestMerkleTree() #1828
        +---[0.00% 0.006771ms ] org.tron.core.capsule.BlockCapsule:setResult() #1829
        +---[0.00% 0.00264ms ] org.tron.core.db.Manager:getDynamicPropertiesStore() #1830
        +---[0.00% 0.01073ms ] org.tron.core.store.DynamicPropertiesStore:getAllowAdaptiveEnergy() #1830
        +---[6.03% 122.235984ms ] org.tron.core.db.Manager:payReward() #1837
        +---[0.00% 0.00246ms ] org.tron.core.ChainBaseManager:getDynamicPropertiesStore() #1839
        +---[0.00% 0.016681ms ] org.tron.core.store.DynamicPropertiesStore:getNextMaintenanceTime() #1839
        +---[0.00% 0.00357ms ] org.tron.core.capsule.BlockCapsule:getTimeStamp() #1840
        +---[0.01% 0.292619ms ] org.tron.consensus.Consensus:applyBlock() #1845
        +---[0.03% 0.569477ms ] org.tron.core.db.Manager:updateTransHashCache() #1853
        +---[0.00% 0.01157ms ] org.tron.core.db.Manager:updateRecentBlock() #1854
        +---[0.11% 2.145307ms ] org.tron.core.db.Manager:updateRecentTransaction() #1855
        +---[0.01% 0.219667ms ] org.tron.core.db.Manager:updateDynamicProperties() #1856
        +---[0.00% 0.003341ms ] org.tron.core.ChainBaseManager:getBalanceTraceStore() #1858
        +---[0.00% 0.01083ms ] org.tron.core.store.BalanceTraceStore:resetCurrentBlockTrace() #1858
        +---[0.00% 0.003461ms ] org.tron.common.parameter.CommonParameter:getInstance() #1860
        `---[0.00% 0.00624ms ] org.tron.common.parameter.CommonParameter:isJsonRpcFilterEnabled() #1860

```

```log
`---ts=2025-08-28 16:59:50.694;thread_name=sync-handle-block;id=90;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@1b6d3586
    `---[125.487493ms] org.tron.core.db.Manager:processBlock()
        +---[0.02% 0.01915ms ] org.tron.consensus.Consensus:validBlock() #1780
        +---[0.00% 0.00115ms ] org.tron.core.ChainBaseManager:getBalanceTraceStore() #1784
        +---[0.00% 0.001491ms ] org.tron.core.store.BalanceTraceStore:initCurrentBlockBalanceTrace() #1784
        +---[0.00% 0.001071ms ] org.tron.core.ChainBaseManager:getDynamicPropertiesStore() #1787
        +---[0.00% 0.002911ms ] org.tron.core.store.DynamicPropertiesStore:saveBlockEnergyUsage() #1787
        +---[37.04% 46.483085ms ] org.tron.core.db.Manager:preValidateTransactionSign() #1791
        +---[0.00% 0.003801ms ] org.tron.core.capsule.TransactionRetCapsule:<init>() #1798
        +---[0.00% 0.00567ms ] org.tron.common.zksnark.MerkleContainer:resetCurrentMerkleTree() #1801
        +---[0.00% 0.00518ms ] org.tron.core.db.accountstate.callback.AccountStateCallBack:preExecute() #1802
        +---[0.00% 0.00136ms ] org.tron.core.capsule.BlockCapsule:getNum() #1804
        +---[0.00% 0.00139ms ] org.tron.core.capsule.BlockCapsule:getTransactions() #1805
        +---[0.17% min=4.29E-4ms,max=0.00154ms,total=0.212417ms,count=376] org.tron.core.ChainBaseManager:getDynamicPropertiesStore() #1806
        +---[0.29% min=7.4E-4ms,max=0.004041ms,total=0.364585ms,count=376] org.tron.core.store.DynamicPropertiesStore:allowConsensusLogicOptimization() #1806
        +---[0.22% min=5.2E-4ms,max=0.00193ms,total=0.282057ms,count=376] org.tron.core.capsule.TransactionCapsule:retCountIsGreatThanContractCount() #1807
        +---[0.14% min=3.69E-4ms,max=0.001229ms,total=0.173451ms,count=376] org.tron.core.capsule.TransactionCapsule:setBlockNum() #1812
        +---[0.15% min=3.7E-4ms,max=0.02113ms,total=0.188814ms,count=376] org.tron.core.db.accountstate.callback.AccountStateCallBack:preExeTrans() #1816
        +---[55.23% min=0.032011ms,max=8.922808ms,total=69.310709ms,count=376] org.tron.core.db.Manager:processTransaction() #1817
        +---[0.20% min=4.21E-4ms,max=0.004721ms,total=0.244901ms,count=376] org.tron.core.db.accountstate.callback.AccountStateCallBack:exeTransFinish() #1818
        +---[0.00% 0.00539ms ] org.tron.core.capsule.TransactionRetCapsule:addAllTransactionInfos() #1823
        +---[0.00% 9.6E-4ms ] org.tron.core.db.accountstate.callback.AccountStateCallBack:executePushFinish() #1824
        +---[0.00% 0.00115ms ] org.tron.core.db.accountstate.callback.AccountStateCallBack:exceptionFinish() #1826
        +---[0.00% 9.69E-4ms ] org.tron.core.capsule.BlockCapsule:getNum() #1828
        +---[2.34% 2.939721ms ] org.tron.common.zksnark.MerkleContainer:saveCurrentMerkleTreeAsBestMerkleTree() #1828
        +---[0.00% 0.00139ms ] org.tron.core.capsule.BlockCapsule:setResult() #1829
        +---[0.00% 0.00135ms ] org.tron.core.db.Manager:getDynamicPropertiesStore() #1830
        +---[0.00% 0.00229ms ] org.tron.core.store.DynamicPropertiesStore:getAllowAdaptiveEnergy() #1830
        +---[1.67% 2.099396ms ] org.tron.core.db.Manager:payReward() #1837
        +---[0.00% 0.00144ms ] org.tron.core.ChainBaseManager:getDynamicPropertiesStore() #1839
        +---[0.00% 0.00151ms ] org.tron.core.store.DynamicPropertiesStore:getNextMaintenanceTime() #1839
        +---[0.00% 0.0011ms ] org.tron.core.capsule.BlockCapsule:getTimeStamp() #1840
        +---[0.17% 0.214987ms ] org.tron.consensus.Consensus:applyBlock() #1845
        +---[0.38% 0.482155ms ] org.tron.core.db.Manager:updateTransHashCache() #1853
        +---[0.00% 0.00509ms ] org.tron.core.db.Manager:updateRecentBlock() #1854
        +---[0.21% 0.257788ms ] org.tron.core.db.Manager:updateRecentTransaction() #1855
        +---[0.09% 0.107303ms ] org.tron.core.db.Manager:updateDynamicProperties() #1856
        +---[0.00% 0.00134ms ] org.tron.core.ChainBaseManager:getBalanceTraceStore() #1858
        +---[0.00% 0.001449ms ] org.tron.core.store.BalanceTraceStore:resetCurrentBlockTrace() #1858
        +---[0.00% 9.4E-4ms ] org.tron.common.parameter.CommonParameter:getInstance() #1860
        `---[0.00% 0.00108ms ] org.tron.common.parameter.CommonParameter:isJsonRpcFilterEnabled() #1860

```

### 完整的大规模快照性能对比表
快照数量 1000次 Get 操作总耗时 平均单次耗时 相比基准性能变化 相比上一级变化 0（基准） 20ms 0.020ms 基准 - 19 39ms 0.039ms +95% +95% 500 23ms 0.023ms +15% -41% 1000 41ms 0.041ms +105% +78% 2000 73ms 0.073ms +265% +78% 3000 100ms 0.100ms +400% +37% 4500 188ms 0.188ms +840% +88%

### 关键发现
1. 1.
   极端性能下降 ：4500 个快照时性能下降达到 840%，相比无快照基准慢了 9.4 倍
2. 2.
   加速恶化趋势 ：从 3000 到 4500 个快照，性能下降了 88%（从 100ms 增加到 188ms），显示出快照数量增加对性能影响的加速恶化
3. 3.
   500 快照异常优化点 ：依然保持最佳性能（23ms），甚至比 19 个快照还要快 41%
4. 4.
   性能下降模式 ：
   
   - 0-500 快照：性能相对稳定
   - 500-2000 快照：线性下降
   - 2000-4500 快照：加速恶化
5. 5.
   大规模影响 ：4500 个快照相比最优的 500 快照性能下降了 717%（从 23ms 增加到 188ms）
### 测试配置
- 账户数量 ：10000 个账户
- 测试操作 ：每个场景执行 1000 次随机 get 操作
- 快照范围 ：19、500、1000、2000、3000、4500 个快照
- 基准对比 ：无快照情况下 20ms
这些结果清楚地展示了大规模快照对 LevelDB 性能的显著影响，特别是在超过 3000 个快照后，性能下降呈现加速恶化的趋势。

 