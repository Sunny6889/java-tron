package org.tron.core.services.http;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.common.application.HttpService;
import org.tron.core.config.args.Args;
import org.tron.core.services.filter.HttpApiAccessFilter;
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

@Component
@Slf4j(topic = "API")
public class SolidityNodeHttpApiService extends HttpService {

  @Autowired
  private GetAccountServlet getAccountServlet;
  @Autowired
  private GetTransactionByIdServlet getTransactionByIdServlet;
  @Autowired
  private GetTransactionInfoByIdServlet getTransactionInfoByIdServlet;
  @Autowired
  private GetTransactionCountByBlockNumServlet getTransactionCountByBlockNumServlet;
  @Autowired
  private GetDelegatedResourceServlet getDelegatedResourceServlet;
  @Autowired
  private GetDelegatedResourceV2Servlet getDelegatedResourceV2Servlet;
  @Autowired
  private GetCanDelegatedMaxSizeServlet getCanDelegatedMaxSizeServlet;
  @Autowired
  private GetAvailableUnfreezeCountServlet getAvailableUnfreezeCountServlet;
  @Autowired
  private GetCanWithdrawUnfreezeAmountServlet getCanWithdrawUnfreezeAmountServlet;
  @Autowired
  private GetDelegatedResourceAccountIndexServlet getDelegatedResourceAccountIndexServlet;
  @Autowired
  private GetDelegatedResourceAccountIndexV2Servlet getDelegatedResourceAccountIndexV2Servlet;
  @Autowired
  private GetExchangeByIdServlet getExchangeByIdServlet;
  @Autowired
  private ListExchangesServlet listExchangesServlet;
  @Autowired
  private ListWitnessesServlet listWitnessesServlet;
  @Autowired
  private GetPaginatedNowWitnessListServlet getPaginatedNowWitnessListServlet;
  @Autowired
  private GetAssetIssueListServlet getAssetIssueListServlet;
  @Autowired
  private GetPaginatedAssetIssueListServlet getPaginatedAssetIssueListServlet;
  @Autowired
  private GetAssetIssueByNameServlet getAssetIssueByNameServlet;
  @Autowired
  private GetAssetIssueByIdServlet getAssetIssueByIdServlet;
  @Autowired
  private GetAssetIssueListByNameServlet getAssetIssueListByNameServlet;
  @Autowired
  private GetNowBlockServlet getNowBlockServlet;
  @Autowired
  private GetBlockByNumServlet getBlockByNumServlet;
  @Autowired
  private GetNodeInfoServlet getNodeInfoServlet;
  @Autowired
  private GetAccountByIdServlet getAccountByIdServlet;
  @Autowired
  private GetBlockByIdServlet getBlockByIdServlet;
  @Autowired
  private GetBlockByLimitNextServlet getBlockByLimitNextServlet;
  @Autowired
  private GetBlockByLatestNumServlet getBlockByLatestNumServlet;
  @Autowired
  private ScanAndMarkNoteByIvkServlet scanAndMarkNoteByIvkServlet;
  @Autowired
  private ScanNoteByIvkServlet scanNoteByIvkServlet;
  @Autowired
  private ScanNoteByOvkServlet scanNoteByOvkServlet;
  @Autowired
  private GetMerkleTreeVoucherInfoServlet getMerkleTreeVoucherInfoServlet;
  @Autowired
  private IsSpendServlet isSpendServlet;
  @Autowired
  private ScanShieldedTRC20NotesByIvkServlet scanShieldedTRC20NotesByIvkServlet;
  @Autowired
  private ScanShieldedTRC20NotesByOvkServlet scanShieldedTRC20NotesByOvkServlet;
  @Autowired
  private IsShieldedTRC20ContractNoteSpentServlet isShieldedTRC20ContractNoteSpentServlet;
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
  private GetBurnTrxServlet getBurnTrxServlet;
  @Autowired
  private GetBrokerageServlet getBrokerageServlet;
  @Autowired
  private GetRewardServlet getRewardServlet;
  @Autowired
  private TriggerConstantContractServlet triggerConstantContractServlet;
  @Autowired
  private EstimateEnergyServlet estimateEnergyServlet;
  @Autowired
  private GetTransactionInfoByBlockNumServlet getTransactionInfoByBlockNumServlet;
  @Autowired
  private HttpApiAccessFilter httpApiAccessFilter;
  @Autowired
  private GetBlockServlet getBlockServlet;
  @Autowired
  private GetBandwidthPricesServlet getBandwidthPricesServlet;
  @Autowired
  private GetEnergyPricesServlet getEnergyPricesServlet;


  public SolidityNodeHttpApiService() {
    port = Args.getInstance().getSolidityHttpPort();
    enable = !isFullNode() && Args.getInstance().isSolidityNodeHttpEnable();
    contextPath = "/";
    maxRequestSize = Args.getInstance().getHttpMaxMessageSize();
  }

  @Override
  protected void addServlet(ServletContextHandler context) {
    // same as FullNode
    context.addServlet(new ServletHolder(getAccountServlet), "/walletsolidity/getaccount");
    context.addServlet(new ServletHolder(listWitnessesServlet), "/walletsolidity/listwitnesses");
    context.addServlet(new ServletHolder(getPaginatedNowWitnessListServlet),
        "/walletsolidity/getpaginatednowwitnesslist");
    context.addServlet(new ServletHolder(getAssetIssueListServlet),
        "/walletsolidity/getassetissuelist");
    context.addServlet(new ServletHolder(getPaginatedAssetIssueListServlet),
        "/walletsolidity/getpaginatedassetissuelist");
    context.addServlet(new ServletHolder(getAssetIssueByNameServlet),
        "/walletsolidity/getassetissuebyname");
    context.addServlet(new ServletHolder(getAssetIssueByIdServlet),
        "/walletsolidity/getassetissuebyid");
    context.addServlet(new ServletHolder(getAssetIssueListByNameServlet),
        "/walletsolidity/getassetissuelistbyname");
    context.addServlet(new ServletHolder(getNowBlockServlet), "/walletsolidity/getnowblock");
    context.addServlet(new ServletHolder(getBlockByNumServlet), "/walletsolidity/getblockbynum");
    context.addServlet(new ServletHolder(getDelegatedResourceServlet),
        "/walletsolidity/getdelegatedresource");
    context.addServlet(new ServletHolder(getDelegatedResourceV2Servlet),
        "/walletsolidity/getdelegatedresourcev2");
    context.addServlet(new ServletHolder(getCanDelegatedMaxSizeServlet),
        "/walletsolidity/getcandelegatedmaxsize");
    context.addServlet(new ServletHolder(getAvailableUnfreezeCountServlet),
        "/walletsolidity/getavailableunfreezecount");
    context.addServlet(new ServletHolder(getCanWithdrawUnfreezeAmountServlet),
        "/walletsolidity/getcanwithdrawunfreezeamount");
    context.addServlet(new ServletHolder(getDelegatedResourceAccountIndexServlet),
        "/walletsolidity/getdelegatedresourceaccountindex");
    context.addServlet(new ServletHolder(getDelegatedResourceAccountIndexV2Servlet),
        "/walletsolidity/getdelegatedresourceaccountindexv2");
    context
        .addServlet(new ServletHolder(getExchangeByIdServlet),
            "/walletsolidity/getexchangebyid");
    context.addServlet(new ServletHolder(listExchangesServlet),
        "/walletsolidity/listexchanges");

    context.addServlet(new ServletHolder(getAccountByIdServlet),
        "/walletsolidity/getaccountbyid");
    context.addServlet(new ServletHolder(getBlockByIdServlet),
        "/walletsolidity/getblockbyid");
    context.addServlet(new ServletHolder(getBlockByLimitNextServlet),
        "/walletsolidity/getblockbylimitnext");
    context.addServlet(new ServletHolder(getBlockByLatestNumServlet),
        "/walletsolidity/getblockbylatestnum");

    // context.addServlet(new ServletHolder(getMerkleTreeVoucherInfoServlet),
    //     "/walletsolidity/getmerkletreevoucherinfo");
    // context.addServlet(new ServletHolder(scanAndMarkNoteByIvkServlet),
    //     "/walletsolidity/scanandmarknotebyivk");
    // context.addServlet(new ServletHolder(scanNoteByIvkServlet),
    //     "/walletsolidity/scannotebyivk");
    // context.addServlet(new ServletHolder(scanNoteByOvkServlet),
    //     "/walletsolidity/scannotebyovk");
    // context.addServlet(new ServletHolder(isSpendServlet),
    //     "/walletsolidity/isspend");
    context.addServlet(new ServletHolder(scanShieldedTRC20NotesByIvkServlet),
        "/walletsolidity/scanshieldedtrc20notesbyivk");
    context.addServlet(new ServletHolder(scanShieldedTRC20NotesByOvkServlet),
        "/walletsolidity/scanshieldedtrc20notesbyovk");
    context.addServlet(new ServletHolder(isShieldedTRC20ContractNoteSpentServlet),
        "/walletsolidity/isshieldedtrc20contractnotespent");

    context.addServlet(new ServletHolder(getTransactionInfoByBlockNumServlet),
        "/walletsolidity/gettransactioninfobyblocknum");

    context.addServlet(new ServletHolder(getMarketOrderByAccountServlet),
        "/walletsolidity/getmarketorderbyaccount");
    context.addServlet(new ServletHolder(getMarketOrderByIdServlet),
        "/walletsolidity/getmarketorderbyid");
    context.addServlet(new ServletHolder(getMarketPriceByPairServlet),
        "/walletsolidity/getmarketpricebypair");
    context.addServlet(new ServletHolder(getMarketOrderListByPairServlet),
        "/walletsolidity/getmarketorderlistbypair");
    context.addServlet(new ServletHolder(getMarketPairListServlet),
        "/walletsolidity/getmarketpairlist");

    // only for SolidityNode
    context.addServlet(new ServletHolder(getTransactionByIdServlet),
        "/walletsolidity/gettransactionbyid");

    context
        .addServlet(new ServletHolder(getTransactionInfoByIdServlet),
            "/walletsolidity/gettransactioninfobyid");
    context
        .addServlet(new ServletHolder(getTransactionCountByBlockNumServlet),
            "/walletsolidity/gettransactioncountbyblocknum");
    context.addServlet(new ServletHolder(triggerConstantContractServlet),
        "/walletsolidity/triggerconstantcontract");
    context.addServlet(new ServletHolder(estimateEnergyServlet),
        "/walletsolidity/estimateenergy");

    context.addServlet(new ServletHolder(getNodeInfoServlet), "/wallet/getnodeinfo");
    context.addServlet(new ServletHolder(getNodeInfoServlet), "/walletsolidity/getnodeinfo");
    context.addServlet(new ServletHolder(getBrokerageServlet), "/walletsolidity/getBrokerage");
    context.addServlet(new ServletHolder(getRewardServlet), "/walletsolidity/getReward");
    context.addServlet(new ServletHolder(getBurnTrxServlet), "/walletsolidity/getburntrx");
    context.addServlet(new ServletHolder(getBlockServlet), "/walletsolidity/getblock");
    context.addServlet(new ServletHolder(getBandwidthPricesServlet),
        "/walletsolidity/getbandwidthprices");
    context.addServlet(new ServletHolder(getEnergyPricesServlet),
        "/walletsolidity/getenergyprices");
  }

  @Override
  protected void addFilter(ServletContextHandler context) {
    // http access filter
    context.addFilter(new FilterHolder(httpApiAccessFilter), "/walletsolidity/*",
        EnumSet.allOf(DispatcherType.class));
    context.getServletHandler().getFilterMappings()[0]
        .setPathSpecs(new String[] {"/walletsolidity/*",
            "/wallet/getnodeinfo"});
  }
}
