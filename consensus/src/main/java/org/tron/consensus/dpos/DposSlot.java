package org.tron.consensus.dpos;


import static org.tron.core.config.Parameter.ChainConstant.BLOCK_PRODUCED_INTERVAL;
import static org.tron.core.config.Parameter.ChainConstant.SINGLE_REPEAT;

import com.google.protobuf.ByteString;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.consensus.ConsensusDelegate;

@Slf4j(topic = "consensus")
@Component
public class DposSlot {

  @Autowired
  private ConsensusDelegate consensusDelegate;

  @Setter
  private DposService dposService;

  public long getAbSlot(long time) {
    return (time - dposService.getGenesisBlockTime()) / BLOCK_PRODUCED_INTERVAL;
  }

  // 获取相对最高区块应该增加几个 slot
  public long getSlot(long time) {
    long firstSlotTime = getTime(1); // 获取接下来1个slot的开始时间，应该开始产块的时间
    if (time < firstSlotTime) {
      return 0;
    }
    return (time - firstSlotTime) / BLOCK_PRODUCED_INTERVAL + 1;
  }

  // 获取接下来几个产块slot的开始时间，应该开始产块的时间
  public long getTime(long slot) { // 理解这个时间的准确性问题
    if (slot == 0) {
      return System.currentTimeMillis();
    }
    long interval = BLOCK_PRODUCED_INTERVAL;

    // 如果最新区块为创世区块，则根据创世块的时间计算
    if (consensusDelegate.getLatestBlockHeaderNumber() == 0) {
      return dposService.getGenesisBlockTime() + slot * interval;
    }

    // 如果处在维护期，增加 slot 到维护期后面
    if (consensusDelegate.lastHeadBlockIsMaintenance()) {
      slot += consensusDelegate.getMaintenanceSkipSlots();
    }

    long time = consensusDelegate.getLatestBlockHeaderTimestamp(); //获取节点最新区块的时间
    time = time - ((time - dposService.getGenesisBlockTime()) % interval); // 抠掉超过interval的处理延时
    return time + interval * slot;
  }

  public ByteString getScheduledWitness(long slot) {
    final long currentSlot = getAbSlot(consensusDelegate.getLatestBlockHeaderTimestamp()) + slot; // 获取应该产的 slot
    if (currentSlot < 0) {
      throw new RuntimeException("current slot should be positive.");
    }
    int size = consensusDelegate.getActiveWitnesses().size();
    if (size <= 0) {
      throw new RuntimeException("active witnesses is null.");
    }
    int witnessIndex = (int) currentSlot % (size * SINGLE_REPEAT);
    witnessIndex /= SINGLE_REPEAT;
    return consensusDelegate.getActiveWitnesses().get(witnessIndex);
  }

}
