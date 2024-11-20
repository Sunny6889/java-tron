package org.tron.consensus.dpos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.common.prometheus.MetricKeys;
import org.tron.common.prometheus.MetricLabels;
import org.tron.common.prometheus.Metrics;
import org.tron.common.utils.StringUtil;
import org.tron.consensus.ConsensusDelegate;
import org.tron.core.capsule.BlockCapsule;
import org.tron.core.capsule.WitnessCapsule;

@Slf4j(topic = "consensus")
@Component
public class StatisticManager {

  @Autowired
  private ConsensusDelegate consensusDelegate;

  @Autowired
  private DposSlot dposSlot;

  public void applyBlock(BlockCapsule blockCapsule) {
    WitnessCapsule wc;
    long blockNum = blockCapsule.getNum();
    long blockTime = blockCapsule.getTimeStamp();
    byte[] blockWitness = blockCapsule.getWitnessAddress().toByteArray();
    wc = consensusDelegate.getWitness(blockWitness);
    wc.setTotalProduced(wc.getTotalProduced() + 1);
    Metrics.counterInc(MetricKeys.Counter.MINER, 1, StringUtil.encode58Check(blockWitness),
        MetricLabels.Counter.MINE_SUCCESS);
    wc.setLatestBlockNum(blockNum);
    wc.setLatestSlotNum(dposSlot.getAbSlot(blockTime));
    consensusDelegate.saveWitness(wc);

    long slot = 1;
    if (blockNum != 1) {
      // 获取相对节点最高区块应该增加几个slot, 正常情况应该是1，如果大于1说明中间有因为产块失败跳过的slot
      slot = dposSlot.getSlot(blockTime);
    }
    for (int i = 1; i < slot; ++i) {
      // 如果大于1把跳过的slot，设置成missed和unfilledblock
      byte[] witness = dposSlot.getScheduledWitness(i).toByteArray();
      wc = consensusDelegate.getWitness(witness);
      wc.setTotalMissed(wc.getTotalMissed() + 1);
      Metrics.counterInc(MetricKeys.Counter.MINER, 1, StringUtil.encode58Check(wc.getAddress()
              .toByteArray()),
          MetricLabels.Counter.MINE_MISS);
      consensusDelegate.saveWitness(wc);
      logger.info("Current block: {}, witness: {}, totalMissed: {}", blockNum,
          StringUtil.encode58Check(wc.getAddress()
              .toByteArray()), wc.getTotalMissed());
      // 标记unfilledblock
      consensusDelegate.applyBlock(false);
    }
    consensusDelegate.applyBlock(true);
  }
}