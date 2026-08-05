package org.tron.core.services.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.http.HttpServlet;
import org.tron.core.services.http.servlets.EstimateEnergyServlet;
import org.tron.core.services.http.servlets.GetAccountByIdServlet;
import org.tron.core.services.http.servlets.GetAccountServlet;
import org.tron.core.services.http.servlets.GetAssetIssueByIdServlet;
import org.tron.core.services.http.servlets.GetAssetIssueByNameServlet;
import org.tron.core.services.http.servlets.GetAssetIssueListByNameServlet;
import org.tron.core.services.http.servlets.GetAssetIssueListServlet;
import org.tron.core.services.http.servlets.GetAvailableUnfreezeCountServlet;
import org.tron.core.services.http.servlets.GetBandwidthPricesServlet;
import org.tron.core.services.http.servlets.GetBlockByIdServlet;
import org.tron.core.services.http.servlets.GetBlockByLatestNumServlet;
import org.tron.core.services.http.servlets.GetBlockByLimitNextServlet;
import org.tron.core.services.http.servlets.GetBlockByNumServlet;
import org.tron.core.services.http.servlets.GetBlockServlet;
import org.tron.core.services.http.servlets.GetBrokerageServlet;
import org.tron.core.services.http.servlets.GetBurnTrxServlet;
import org.tron.core.services.http.servlets.GetCanDelegatedMaxSizeServlet;
import org.tron.core.services.http.servlets.GetCanWithdrawUnfreezeAmountServlet;
import org.tron.core.services.http.servlets.GetDelegatedResourceAccountIndexServlet;
import org.tron.core.services.http.servlets.GetDelegatedResourceAccountIndexV2Servlet;
import org.tron.core.services.http.servlets.GetDelegatedResourceServlet;
import org.tron.core.services.http.servlets.GetDelegatedResourceV2Servlet;
import org.tron.core.services.http.servlets.GetEnergyPricesServlet;
import org.tron.core.services.http.servlets.GetExchangeByIdServlet;
import org.tron.core.services.http.servlets.GetMarketOrderByAccountServlet;
import org.tron.core.services.http.servlets.GetMarketOrderByIdServlet;
import org.tron.core.services.http.servlets.GetMarketOrderListByPairServlet;
import org.tron.core.services.http.servlets.GetMarketPairListServlet;
import org.tron.core.services.http.servlets.GetMarketPriceByPairServlet;
import org.tron.core.services.http.servlets.GetMerkleTreeVoucherInfoServlet;
import org.tron.core.services.http.servlets.GetNodeInfoServlet;
import org.tron.core.services.http.servlets.GetNowBlockServlet;
import org.tron.core.services.http.servlets.GetPaginatedAssetIssueListServlet;
import org.tron.core.services.http.servlets.GetPaginatedNowWitnessListServlet;
import org.tron.core.services.http.servlets.GetRewardServlet;
import org.tron.core.services.http.servlets.GetTransactionByIdServlet;
import org.tron.core.services.http.servlets.GetTransactionCountByBlockNumServlet;
import org.tron.core.services.http.servlets.GetTransactionInfoByBlockNumServlet;
import org.tron.core.services.http.servlets.GetTransactionInfoByIdServlet;
import org.tron.core.services.http.servlets.IsShieldedTRC20ContractNoteSpentServlet;
import org.tron.core.services.http.servlets.IsSpendServlet;
import org.tron.core.services.http.servlets.ListExchangesServlet;
import org.tron.core.services.http.servlets.ListWitnessesServlet;
import org.tron.core.services.http.servlets.ScanAndMarkNoteByIvkServlet;
import org.tron.core.services.http.servlets.ScanNoteByIvkServlet;
import org.tron.core.services.http.servlets.ScanNoteByOvkServlet;
import org.tron.core.services.http.servlets.ScanShieldedTRC20NotesByIvkServlet;
import org.tron.core.services.http.servlets.ScanShieldedTRC20NotesByOvkServlet;
import org.tron.core.services.http.servlets.TriggerConstantContractServlet;

/**
 * Declarative registry of the node's http endpoints: one row per endpoint, carrying its path
 * suffix, servlet class, access nature and the surfaces (http services) it is exposed on.
 *
 * <p>The four http services build their servlet mappings from this table instead of keeping a
 * hand-written list each, so an endpoint is declared exactly once and every surface stays in
 * sync by construction. Rows marked BUILD or WRITE must stay FULL-only: the cursor surfaces
 * (SOLIDITY / PBFT) must never run a write path on a cursor-switched thread, and the standalone
 * SolidityNode surface only syncs solidified blocks and cannot propagate transactions.
 */
public enum HttpApiDef {

  GET_ACCOUNT("getaccount", GetAccountServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  LIST_WITNESSES("listwitnesses", ListWitnessesServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_PAGINATED_NOW_WITNESS_LIST(
      "getpaginatednowwitnesslist",
      GetPaginatedNowWitnessListServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.SOLIDITY_NODE),

  GET_ASSET_ISSUE_LIST("getassetissuelist", GetAssetIssueListServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_PAGINATED_ASSET_ISSUE_LIST(
      "getpaginatedassetissuelist",
      GetPaginatedAssetIssueListServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_ASSET_ISSUE_BY_NAME("getassetissuebyname", GetAssetIssueByNameServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_ASSET_ISSUE_BY_ID("getassetissuebyid", GetAssetIssueByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_ASSET_ISSUE_LIST_BY_NAME("getassetissuelistbyname", GetAssetIssueListByNameServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_NOW_BLOCK("getnowblock", GetNowBlockServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK_BY_NUM("getblockbynum", GetBlockByNumServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_DELEGATED_RESOURCE("getdelegatedresource", GetDelegatedResourceServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_DELEGATED_RESOURCE_V2("getdelegatedresourcev2", GetDelegatedResourceV2Servlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_CAN_DELEGATED_MAX_SIZE("getcandelegatedmaxsize", GetCanDelegatedMaxSizeServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_AVAILABLE_UNFREEZE_COUNT("getavailableunfreezecount", GetAvailableUnfreezeCountServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_CAN_WITHDRAW_UNFREEZE_AMOUNT(
      "getcanwithdrawunfreezeamount",
      GetCanWithdrawUnfreezeAmountServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_DELEGATED_RESOURCE_ACCOUNT_INDEX(
      "getdelegatedresourceaccountindex",
      GetDelegatedResourceAccountIndexServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_DELEGATED_RESOURCE_ACCOUNT_INDEX_V2(
      "getdelegatedresourceaccountindexv2",
      GetDelegatedResourceAccountIndexV2Servlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_EXCHANGE_BY_ID("getexchangebyid", GetExchangeByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  LIST_EXCHANGES("listexchanges", ListExchangesServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_ACCOUNT_BY_ID("getaccountbyid", GetAccountByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK_BY_ID("getblockbyid", GetBlockByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK_BY_LIMIT_NEXT("getblockbylimitnext", GetBlockByLimitNextServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK_BY_LATEST_NUM("getblockbylatestnum", GetBlockByLatestNumServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  SCAN_SHIELDED_TRC20_NOTES_BY_IVK(
      "scanshieldedtrc20notesbyivk",
      ScanShieldedTRC20NotesByIvkServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  SCAN_SHIELDED_TRC20_NOTES_BY_OVK(
      "scanshieldedtrc20notesbyovk",
      ScanShieldedTRC20NotesByOvkServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  IS_SHIELDED_TRC20_CONTRACT_NOTE_SPENT(
      "isshieldedtrc20contractnotespent",
      IsShieldedTRC20ContractNoteSpentServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_TRANSACTION_INFO_BY_BLOCK_NUM(
      "gettransactioninfobyblocknum",
      GetTransactionInfoByBlockNumServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.SOLIDITY_NODE),

  GET_MARKET_ORDER_BY_ACCOUNT("getmarketorderbyaccount", GetMarketOrderByAccountServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_MARKET_ORDER_BY_ID("getmarketorderbyid", GetMarketOrderByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_MARKET_PRICE_BY_PAIR("getmarketpricebypair", GetMarketPriceByPairServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_MARKET_ORDER_LIST_BY_PAIR("getmarketorderlistbypair", GetMarketOrderListByPairServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_MARKET_PAIR_LIST("getmarketpairlist", GetMarketPairListServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),


  GET_TRANSACTION_BY_ID("gettransactionbyid", GetTransactionByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_TRANSACTION_INFO_BY_ID("gettransactioninfobyid", GetTransactionInfoByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_TRANSACTION_COUNT_BY_BLOCK_NUM(
      "gettransactioncountbyblocknum",
      GetTransactionCountByBlockNumServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  TRIGGER_CONSTANT_CONTRACT("triggerconstantcontract", TriggerConstantContractServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  ESTIMATE_ENERGY("estimateenergy", EstimateEnergyServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_NODE_INFO("getnodeinfo", GetNodeInfoServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BROKERAGE("getBrokerage", GetBrokerageServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_REWARD("getReward", GetRewardServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BURN_TRX("getburntrx", GetBurnTrxServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK("getblock", GetBlockServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BANDWIDTH_PRICES("getbandwidthprices", GetBandwidthPricesServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_ENERGY_PRICES("getenergyprices", GetEnergyPricesServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_MERKLE_TREE_VOUCHER_INFO("getmerkletreevoucherinfo", GetMerkleTreeVoucherInfoServlet.class,
      Access.READ, Surface.PBFT),

  SCAN_AND_MARK_NOTE_BY_IVK("scanandmarknotebyivk", ScanAndMarkNoteByIvkServlet.class,
      Access.READ, Surface.PBFT),

  SCAN_NOTE_BY_IVK("scannotebyivk", ScanNoteByIvkServlet.class,
      Access.READ, Surface.FULL, Surface.PBFT),

  SCAN_NOTE_BY_OVK("scannotebyovk", ScanNoteByOvkServlet.class,
      Access.READ, Surface.FULL, Surface.PBFT),

  IS_SPEND("isspend", IsSpendServlet.class,
      Access.READ, Surface.FULL, Surface.PBFT);

  /** Http services an endpoint can be exposed on. */
  public enum Surface {
    FULL, SOLIDITY, PBFT, SOLIDITY_NODE
  }

  /** READ: query only; BUILD: creates an unsigned transaction; WRITE: pushes into the chain. */
  public enum Access {
    READ, BUILD, WRITE
  }

  private final String suffix;
  private final Class<? extends HttpServlet> servlet;
  private final Access access;
  private final EnumSet<Surface> surfaces;

  HttpApiDef(String suffix, Class<? extends HttpServlet> servlet, Access access,
      Surface first, Surface... rest) {
    this.suffix = suffix;
    this.servlet = servlet;
    this.access = access;
    this.surfaces = EnumSet.of(first, rest);
  }

  public static List<HttpApiDef> forSurface(Surface surface) {
    List<HttpApiDef> result = new ArrayList<>();
    for (HttpApiDef def : values()) {
      if (def.surfaces.contains(surface)) {
        result.add(def);
      }
    }
    return Collections.unmodifiableList(result);
  }

  public String getSuffix() {
    return suffix;
  }

  public Class<? extends HttpServlet> getServlet() {
    return servlet;
  }

  public Access getAccess() {
    return access;
  }
}
