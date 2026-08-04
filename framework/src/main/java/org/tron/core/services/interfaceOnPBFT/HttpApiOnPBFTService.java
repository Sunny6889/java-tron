package org.tron.core.services.interfaceOnPBFT;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.tron.common.application.HttpService;
import org.tron.core.config.args.Args;
import org.tron.core.services.filter.HttpApiAccessFilter;
import org.tron.core.services.filter.LiteFnQueryHttpFilter;
import org.tron.core.services.filter.PbftCursorFilter;
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
import org.tron.core.services.http.servlets.GetRewardServlet;
import org.tron.core.services.http.servlets.GetTransactionByIdServlet;
import org.tron.core.services.http.servlets.GetTransactionCountByBlockNumServlet;
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

@Slf4j(topic = "API")
public class HttpApiOnPBFTService extends HttpService {

  @Autowired
  private GetAccountServlet accountServlet;

  @Autowired
  private GetTransactionByIdServlet getTransactionByIdServlet;
  @Autowired
  private GetTransactionInfoByIdServlet getTransactionInfoByIdServlet;
  @Autowired
  private ListWitnessesServlet listWitnessesServlet;
  @Autowired
  private GetAssetIssueListServlet getAssetIssueListServlet;
  @Autowired
  private GetPaginatedAssetIssueListServlet getPaginatedAssetIssueListServlet;
  @Autowired
  private GetNowBlockServlet getNowBlockServlet;
  @Autowired
  private GetBlockByNumServlet getBlockByNumServlet;

  @Autowired
  private GetNodeInfoServlet getNodeInfoServlet;

  @Autowired
  private GetDelegatedResourceServlet getDelegatedResourceServlet;
  @Autowired
  private GetDelegatedResourceAccountIndexServlet getDelegatedResourceAccountIndexServlet;
  @Autowired
  private GetExchangeByIdServlet getExchangeByIdServlet;
  @Autowired
  private ListExchangesServlet listExchangesServlet;
  @Autowired
  private GetTransactionCountByBlockNumServlet getTransactionCountByBlockNumServlet;
  @Autowired
  private GetAssetIssueByNameServlet getAssetIssueByNameServlet;
  @Autowired
  private GetAssetIssueByIdServlet getAssetIssueByIdServlet;
  @Autowired
  private GetAssetIssueListByNameServlet getAssetIssueListByNameServlet;
  @Autowired
  private GetAccountByIdServlet getAccountByIdServlet;
  @Autowired
  private GetBlockByIdServlet getBlockByIdServlet;
  @Autowired
  private GetBlockByLimitNextServlet getBlockByLimitNextServlet;
  @Autowired
  private GetBlockByLatestNumServlet getBlockByLatestNumServlet;
  @Autowired
  private GetMerkleTreeVoucherInfoServlet getMerkleTreeVoucherInfoServlet;
  @Autowired
  private ScanNoteByIvkServlet scanNoteByIvkServlet;
  @Autowired
  private ScanAndMarkNoteByIvkServlet scanAndMarkNoteByIvkServlet;
  @Autowired
  private ScanNoteByOvkServlet scanNoteByOvkServlet;
  @Autowired
  private IsSpendServlet isSpendServlet;
  @Autowired
  private GetBrokerageServlet getBrokerageServlet;
  @Autowired
  private GetRewardServlet getRewardServlet;
  @Autowired
  private TriggerConstantContractServlet triggerConstantContractServlet;
  @Autowired
  private EstimateEnergyServlet estimateEnergyServlet;
  @Autowired
  private LiteFnQueryHttpFilter liteFnQueryHttpFilter;
  @Autowired
  private HttpApiAccessFilter httpApiAccessFilter;
  @Autowired
  private PbftCursorFilter pbftCursorFilter;

  @Autowired
  private GetMarketOrderByAccountServlet getMarketOrderByAccountServlet;
  @Autowired
  private GetMarketOrderByIdServlet getMarketOrderByIdServlet;
  @Autowired
  private GetMarketPriceByPairServlet getMarketPriceByPairServlet;
  @Autowired
  private GetMarketOrderListByPairServlet getMarketOrderListByPairServlet;
  @Autowired
  private GetMarketPairListServlet getMarketPairListServlet;

  @Autowired
  private ScanShieldedTRC20NotesByIvkServlet scanShieldedTRC20NotesByIvkServlet;
  @Autowired
  private ScanShieldedTRC20NotesByOvkServlet scanShieldedTRC20NotesByOvkServlet;
  @Autowired
  private IsShieldedTRC20ContractNoteSpentServlet
      isShieldedTRC20ContractNoteSpentServlet;
  @Autowired
  private GetBurnTrxServlet getBurnTrxServlet;
  @Autowired
  private GetBandwidthPricesServlet getBandwidthPricesServlet;
  @Autowired
  private GetEnergyPricesServlet getEnergyPricesServlet;

  @Autowired
  private GetBlockServlet getBlockServlet;

  @Autowired
  private GetAvailableUnfreezeCountServlet getAvailableUnfreezeCountServlet;
  @Autowired
  private GetCanDelegatedMaxSizeServlet getCanDelegatedMaxSizeServlet;
  @Autowired
  private GetCanWithdrawUnfreezeAmountServlet getCanWithdrawUnfreezeAmountServlet;
  @Autowired
  private GetDelegatedResourceAccountIndexV2Servlet getDelegatedResourceAccountIndexV2Servlet;
  @Autowired
  private GetDelegatedResourceV2Servlet getDelegatedResourceV2Servlet;

  public HttpApiOnPBFTService() {
    port = Args.getInstance().getPBFTHttpPort();
    enable = isFullNode() && Args.getInstance().isPBFTHttpEnable();
    contextPath = "/walletpbft";
    maxRequestSize = Args.getInstance().getHttpMaxMessageSize();
  }

  @Override
  protected void addServlet(ServletContextHandler context) {
    // same as FullNode
    context.addServlet(new ServletHolder(accountServlet), "/getaccount");
    context.addServlet(new ServletHolder(listWitnessesServlet), "/listwitnesses");
    context.addServlet(new ServletHolder(getAssetIssueListServlet), "/getassetissuelist");
    context.addServlet(new ServletHolder(getPaginatedAssetIssueListServlet),
        "/getpaginatedassetissuelist");
    context
        .addServlet(new ServletHolder(getAssetIssueByNameServlet), "/getassetissuebyname");
    context.addServlet(new ServletHolder(getAssetIssueByIdServlet), "/getassetissuebyid");
    context.addServlet(new ServletHolder(getAssetIssueListByNameServlet),
        "/getassetissuelistbyname");
    context.addServlet(new ServletHolder(getNowBlockServlet), "/getnowblock");
    context.addServlet(new ServletHolder(getBlockByNumServlet), "/getblockbynum");
    context.addServlet(new ServletHolder(getDelegatedResourceServlet),
        "/getdelegatedresource");
    context.addServlet(new ServletHolder(getDelegatedResourceAccountIndexServlet),
        "/getdelegatedresourceaccountindex");
    context.addServlet(new ServletHolder(getExchangeByIdServlet), "/getexchangebyid");
    context.addServlet(new ServletHolder(listExchangesServlet), "/listexchanges");
    context.addServlet(new ServletHolder(getAccountByIdServlet), "/getaccountbyid");
    context.addServlet(new ServletHolder(getBlockByIdServlet), "/getblockbyid");
    context
        .addServlet(new ServletHolder(getBlockByLimitNextServlet), "/getblockbylimitnext");
    context
        .addServlet(new ServletHolder(getBlockByLatestNumServlet), "/getblockbylatestnum");
    context.addServlet(new ServletHolder(getMerkleTreeVoucherInfoServlet),
        "/getmerkletreevoucherinfo");
    context.addServlet(new ServletHolder(scanAndMarkNoteByIvkServlet),
        "/scanandmarknotebyivk");
    context.addServlet(new ServletHolder(scanNoteByIvkServlet), "/scannotebyivk");
    context.addServlet(new ServletHolder(scanNoteByOvkServlet), "/scannotebyovk");
    context.addServlet(new ServletHolder(isSpendServlet), "/isspend");
    context.addServlet(new ServletHolder(triggerConstantContractServlet),
        "/triggerconstantcontract");
    context.addServlet(new ServletHolder(estimateEnergyServlet), "/estimateenergy");

    // only for PBFTNode
    context.addServlet(new ServletHolder(getTransactionByIdServlet), "/gettransactionbyid");
    context.addServlet(new ServletHolder(getTransactionInfoByIdServlet),
        "/gettransactioninfobyid");

    context.addServlet(new ServletHolder(getTransactionCountByBlockNumServlet),
        "/gettransactioncountbyblocknum");

    context.addServlet(new ServletHolder(getNodeInfoServlet), "/getnodeinfo");
    context.addServlet(new ServletHolder(getBrokerageServlet), "/getBrokerage");
    context.addServlet(new ServletHolder(getRewardServlet), "/getReward");

    context.addServlet(new ServletHolder(getMarketOrderByAccountServlet),
        "/getmarketorderbyaccount");
    context.addServlet(new ServletHolder(getMarketOrderByIdServlet),
        "/getmarketorderbyid");
    context.addServlet(new ServletHolder(getMarketPriceByPairServlet),
        "/getmarketpricebypair");
    context.addServlet(new ServletHolder(getMarketOrderListByPairServlet),
        "/getmarketorderlistbypair");
    context.addServlet(new ServletHolder(getMarketPairListServlet),
        "/getmarketpairlist");

    context.addServlet(new ServletHolder(scanShieldedTRC20NotesByIvkServlet),
        "/scanshieldedtrc20notesbyivk");
    context.addServlet(new ServletHolder(scanShieldedTRC20NotesByOvkServlet),
        "/scanshieldedtrc20notesbyovk");
    context.addServlet(new ServletHolder(isShieldedTRC20ContractNoteSpentServlet),
        "/isshieldedtrc20contractnotespent");
    context.addServlet(new ServletHolder(getBurnTrxServlet),
        "/getburntrx");
    context.addServlet(new ServletHolder(getBandwidthPricesServlet),
        "/getbandwidthprices");
    context.addServlet(new ServletHolder(getEnergyPricesServlet),
        "/getenergyprices");
    context.addServlet(new ServletHolder(getBlockServlet),
        "/getblock");

    context.addServlet(new ServletHolder(getAvailableUnfreezeCountServlet),
        "/getavailableunfreezecount");
    context.addServlet(new ServletHolder(getCanDelegatedMaxSizeServlet),
        "/getcandelegatedmaxsize");
    context.addServlet(new ServletHolder(getCanWithdrawUnfreezeAmountServlet),
        "/getcanwithdrawunfreezeamount");
    context.addServlet(new ServletHolder(getDelegatedResourceAccountIndexV2Servlet),
        "/getdelegatedresourceaccountindexv2");
    context.addServlet(new ServletHolder(getDelegatedResourceV2Servlet),
        "/getdelegatedresourcev2");
  }

  @Override
  protected void addFilter(ServletContextHandler context) {
    // filters the specified APIs
    // when node is lite fullnode and openHistoryQueryWhenLiteFN is false
    context.addFilter(new FilterHolder(liteFnQueryHttpFilter), "/*",
        EnumSet.allOf(DispatcherType.class));

    // api access filter
    context.addFilter(new FilterHolder(httpApiAccessFilter), "/*",
        EnumSet.allOf(DispatcherType.class));

    // every request on this port reads the PBFT state view
    context.addFilter(new FilterHolder(pbftCursorFilter), "/*",
        EnumSet.allOf(DispatcherType.class));
  }
}
