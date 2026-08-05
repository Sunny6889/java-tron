package org.tron.core.services.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.http.HttpServlet;
import org.tron.core.services.http.servlets.AccountPermissionUpdateServlet;
import org.tron.core.services.http.servlets.BroadcastHexServlet;
import org.tron.core.services.http.servlets.BroadcastServlet;
import org.tron.core.services.http.servlets.CancelAllUnfreezeV2Servlet;
import org.tron.core.services.http.servlets.ClearABIServlet;
import org.tron.core.services.http.servlets.CreateAccountServlet;
import org.tron.core.services.http.servlets.CreateAssetIssueServlet;
import org.tron.core.services.http.servlets.CreateCommonTransactionServlet;
import org.tron.core.services.http.servlets.CreateShieldedContractParametersServlet;
import org.tron.core.services.http.servlets.CreateShieldedContractParametersWithoutAskServlet;
import org.tron.core.services.http.servlets.CreateSpendAuthSigServlet;
import org.tron.core.services.http.servlets.CreateWitnessServlet;
import org.tron.core.services.http.servlets.DelegateResourceServlet;
import org.tron.core.services.http.servlets.DeployContractServlet;
import org.tron.core.services.http.servlets.EstimateEnergyServlet;
import org.tron.core.services.http.servlets.ExchangeCreateServlet;
import org.tron.core.services.http.servlets.ExchangeInjectServlet;
import org.tron.core.services.http.servlets.ExchangeTransactionServlet;
import org.tron.core.services.http.servlets.ExchangeWithdrawServlet;
import org.tron.core.services.http.servlets.FreezeBalanceServlet;
import org.tron.core.services.http.servlets.FreezeBalanceV2Servlet;
import org.tron.core.services.http.servlets.GetAccountBalanceServlet;
import org.tron.core.services.http.servlets.GetAccountByIdServlet;
import org.tron.core.services.http.servlets.GetAccountNetServlet;
import org.tron.core.services.http.servlets.GetAccountResourceServlet;
import org.tron.core.services.http.servlets.GetAccountServlet;
import org.tron.core.services.http.servlets.GetAkFromAskServlet;
import org.tron.core.services.http.servlets.GetAssetIssueByAccountServlet;
import org.tron.core.services.http.servlets.GetAssetIssueByIdServlet;
import org.tron.core.services.http.servlets.GetAssetIssueByNameServlet;
import org.tron.core.services.http.servlets.GetAssetIssueListByNameServlet;
import org.tron.core.services.http.servlets.GetAssetIssueListServlet;
import org.tron.core.services.http.servlets.GetAvailableUnfreezeCountServlet;
import org.tron.core.services.http.servlets.GetBandwidthPricesServlet;
import org.tron.core.services.http.servlets.GetBlockBalanceServlet;
import org.tron.core.services.http.servlets.GetBlockByIdServlet;
import org.tron.core.services.http.servlets.GetBlockByLatestNumServlet;
import org.tron.core.services.http.servlets.GetBlockByLimitNextServlet;
import org.tron.core.services.http.servlets.GetBlockByNumServlet;
import org.tron.core.services.http.servlets.GetBlockServlet;
import org.tron.core.services.http.servlets.GetBrokerageServlet;
import org.tron.core.services.http.servlets.GetBurnTrxServlet;
import org.tron.core.services.http.servlets.GetCanDelegatedMaxSizeServlet;
import org.tron.core.services.http.servlets.GetCanWithdrawUnfreezeAmountServlet;
import org.tron.core.services.http.servlets.GetChainParametersServlet;
import org.tron.core.services.http.servlets.GetContractInfoServlet;
import org.tron.core.services.http.servlets.GetContractServlet;
import org.tron.core.services.http.servlets.GetDelegatedResourceAccountIndexServlet;
import org.tron.core.services.http.servlets.GetDelegatedResourceAccountIndexV2Servlet;
import org.tron.core.services.http.servlets.GetDelegatedResourceServlet;
import org.tron.core.services.http.servlets.GetDelegatedResourceV2Servlet;
import org.tron.core.services.http.servlets.GetDiversifierServlet;
import org.tron.core.services.http.servlets.GetEnergyPricesServlet;
import org.tron.core.services.http.servlets.GetExchangeByIdServlet;
import org.tron.core.services.http.servlets.GetExpandedSpendingKeyServlet;
import org.tron.core.services.http.servlets.GetIncomingViewingKeyServlet;
import org.tron.core.services.http.servlets.GetMarketOrderByAccountServlet;
import org.tron.core.services.http.servlets.GetMarketOrderByIdServlet;
import org.tron.core.services.http.servlets.GetMarketOrderListByPairServlet;
import org.tron.core.services.http.servlets.GetMarketPairListServlet;
import org.tron.core.services.http.servlets.GetMarketPriceByPairServlet;
import org.tron.core.services.http.servlets.GetMemoFeePricesServlet;
import org.tron.core.services.http.servlets.GetNewShieldedAddressServlet;
import org.tron.core.services.http.servlets.GetNextMaintenanceTimeServlet;
import org.tron.core.services.http.servlets.GetNkFromNskServlet;
import org.tron.core.services.http.servlets.GetNodeInfoServlet;
import org.tron.core.services.http.servlets.GetNowBlockServlet;
import org.tron.core.services.http.servlets.GetPaginatedAssetIssueListServlet;
import org.tron.core.services.http.servlets.GetPaginatedExchangeListServlet;
import org.tron.core.services.http.servlets.GetPaginatedNowWitnessListServlet;
import org.tron.core.services.http.servlets.GetPaginatedProposalListServlet;
import org.tron.core.services.http.servlets.GetPendingSizeServlet;
import org.tron.core.services.http.servlets.GetProposalByIdServlet;
import org.tron.core.services.http.servlets.GetRcmServlet;
import org.tron.core.services.http.servlets.GetRewardServlet;
import org.tron.core.services.http.servlets.GetSpendingKeyServlet;
import org.tron.core.services.http.servlets.GetTransactionApprovedListServlet;
import org.tron.core.services.http.servlets.GetTransactionByIdServlet;
import org.tron.core.services.http.servlets.GetTransactionCountByBlockNumServlet;
import org.tron.core.services.http.servlets.GetTransactionFromPendingServlet;
import org.tron.core.services.http.servlets.GetTransactionInfoByBlockNumServlet;
import org.tron.core.services.http.servlets.GetTransactionInfoByIdServlet;
import org.tron.core.services.http.servlets.GetTransactionListFromPendingServlet;
import org.tron.core.services.http.servlets.GetTransactionReceiptByIdServlet;
import org.tron.core.services.http.servlets.GetTransactionSignWeightServlet;
import org.tron.core.services.http.servlets.GetTriggerInputForShieldedTRC20ContractServlet;
import org.tron.core.services.http.servlets.GetZenPaymentAddressServlet;
import org.tron.core.services.http.servlets.IsShieldedTRC20ContractNoteSpentServlet;
import org.tron.core.services.http.servlets.ListExchangesServlet;
import org.tron.core.services.http.servlets.ListNodesServlet;
import org.tron.core.services.http.servlets.ListProposalsServlet;
import org.tron.core.services.http.servlets.ListWitnessesServlet;
import org.tron.core.services.http.servlets.MarketCancelOrderServlet;
import org.tron.core.services.http.servlets.MarketSellAssetServlet;
import org.tron.core.services.http.servlets.ParticipateAssetIssueServlet;
import org.tron.core.services.http.servlets.ProposalApproveServlet;
import org.tron.core.services.http.servlets.ProposalCreateServlet;
import org.tron.core.services.http.servlets.ProposalDeleteServlet;
import org.tron.core.services.http.servlets.ScanShieldedTRC20NotesByIvkServlet;
import org.tron.core.services.http.servlets.ScanShieldedTRC20NotesByOvkServlet;
import org.tron.core.services.http.servlets.SetAccountIdServlet;
import org.tron.core.services.http.servlets.TotalTransactionServlet;
import org.tron.core.services.http.servlets.TransferAssetServlet;
import org.tron.core.services.http.servlets.TransferServlet;
import org.tron.core.services.http.servlets.TriggerConstantContractServlet;
import org.tron.core.services.http.servlets.TriggerSmartContractServlet;
import org.tron.core.services.http.servlets.UnDelegateResourceServlet;
import org.tron.core.services.http.servlets.UnFreezeAssetServlet;
import org.tron.core.services.http.servlets.UnFreezeBalanceServlet;
import org.tron.core.services.http.servlets.UnFreezeBalanceV2Servlet;
import org.tron.core.services.http.servlets.UpdateAccountServlet;
import org.tron.core.services.http.servlets.UpdateAssetServlet;
import org.tron.core.services.http.servlets.UpdateBrokerageServlet;
import org.tron.core.services.http.servlets.UpdateEnergyLimitServlet;
import org.tron.core.services.http.servlets.UpdateSettingServlet;
import org.tron.core.services.http.servlets.UpdateWitnessServlet;
import org.tron.core.services.http.servlets.ValidateAddressServlet;
import org.tron.core.services.http.servlets.VoteWitnessAccountServlet;
import org.tron.core.services.http.servlets.WithdrawBalanceServlet;
import org.tron.core.services.http.servlets.WithdrawExpireUnfreezeServlet;

/**
 * Declarative registry of the node's http endpoints: one row per endpoint, carrying its path
 * suffix, servlet class, access nature and the surfaces (http services) it is exposed on.
 *
 * <p>Different http services build their servlet mappings from this table instead of keeping a
 * list each, so an endpoint is declared exactly once and every surface stays in
 * sync by construction.
 *
 * <p>Rows marked BUILD or WRITE may declare the FULL surface only — the one whose threads always
 * read with the default HEAD cursor. The cursor surfaces (SOLIDITY / PBFT) must never run a
 * write path on a cursor-switched thread, and the standalone SolidityNode surface only syncs
 * solidified blocks and cannot propagate transactions.
 */
public enum HttpApiDef {

  GET_ACCOUNT("getaccount", GetAccountServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  TRANSFER("createtransaction", TransferServlet.class,
      Access.BUILD, Surface.FULL),

  BROADCAST("broadcasttransaction", BroadcastServlet.class,
      Access.WRITE, Surface.FULL),

  UPDATE_ACCOUNT("updateaccount", UpdateAccountServlet.class,
      Access.BUILD, Surface.FULL),

  VOTE_WITNESS_ACCOUNT("votewitnessaccount", VoteWitnessAccountServlet.class,
      Access.BUILD, Surface.FULL),

  CREATE_ASSET_ISSUE("createassetissue", CreateAssetIssueServlet.class,
      Access.BUILD, Surface.FULL),

  UPDATE_WITNESS("updatewitness", UpdateWitnessServlet.class,
      Access.BUILD, Surface.FULL),

  CREATE_ACCOUNT("createaccount", CreateAccountServlet.class,
      Access.BUILD, Surface.FULL),

  CREATE_WITNESS("createwitness", CreateWitnessServlet.class,
      Access.BUILD, Surface.FULL),

  TRANSFER_ASSET("transferasset", TransferAssetServlet.class,
      Access.BUILD, Surface.FULL),

  PARTICIPATE_ASSET_ISSUE("participateassetissue", ParticipateAssetIssueServlet.class,
      Access.BUILD, Surface.FULL),

  FREEZE_BALANCE("freezebalance", FreezeBalanceServlet.class,
      Access.BUILD, Surface.FULL),

  UN_FREEZE_BALANCE("unfreezebalance", UnFreezeBalanceServlet.class,
      Access.BUILD, Surface.FULL),

  UN_FREEZE_ASSET("unfreezeasset", UnFreezeAssetServlet.class,
      Access.BUILD, Surface.FULL),

  WITHDRAW_BALANCE("withdrawbalance", WithdrawBalanceServlet.class,
      Access.BUILD, Surface.FULL),

  UPDATE_ASSET("updateasset", UpdateAssetServlet.class,
      Access.BUILD, Surface.FULL),

  LIST_NODES("listnodes", ListNodesServlet.class,
      Access.READ, Surface.FULL),

  GET_ASSET_ISSUE_BY_ACCOUNT("getassetissuebyaccount", GetAssetIssueByAccountServlet.class,
      Access.READ, Surface.FULL),

  GET_ACCOUNT_NET("getaccountnet", GetAccountNetServlet.class,
      Access.READ, Surface.FULL),

  GET_ASSET_ISSUE_BY_NAME("getassetissuebyname", GetAssetIssueByNameServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_ASSET_ISSUE_LIST_BY_NAME("getassetissuelistbyname", GetAssetIssueListByNameServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_ASSET_ISSUE_BY_ID("getassetissuebyid", GetAssetIssueByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_NOW_BLOCK("getnowblock", GetNowBlockServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK_BY_NUM("getblockbynum", GetBlockByNumServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK_BY_ID("getblockbyid", GetBlockByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK_BY_LIMIT_NEXT("getblockbylimitnext", GetBlockByLimitNextServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK_BY_LATEST_NUM("getblockbylatestnum", GetBlockByLatestNumServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_TRANSACTION_BY_ID("gettransactionbyid", GetTransactionByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_TRANSACTION_INFO_BY_ID("gettransactioninfobyid", GetTransactionInfoByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_TRANSACTION_RECEIPT_BY_ID("gettransactionreceiptbyid", GetTransactionReceiptByIdServlet.class,
      Access.READ, Surface.FULL),

  GET_TRANSACTION_COUNT_BY_BLOCK_NUM(
      "gettransactioncountbyblocknum",
      GetTransactionCountByBlockNumServlet.class,
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

  GET_PAGINATED_PROPOSAL_LIST("getpaginatedproposallist", GetPaginatedProposalListServlet.class,
      Access.READ, Surface.FULL),

  GET_PAGINATED_EXCHANGE_LIST("getpaginatedexchangelist", GetPaginatedExchangeListServlet.class,
      Access.READ, Surface.FULL),

  TOTAL_TRANSACTION("totaltransaction", TotalTransactionServlet.class,
      Access.READ, Surface.FULL),

  GET_NEXT_MAINTENANCE_TIME("getnextmaintenancetime", GetNextMaintenanceTimeServlet.class,
      Access.READ, Surface.FULL),

  VALIDATE_ADDRESS("validateaddress", ValidateAddressServlet.class,
      Access.READ, Surface.FULL),

  DEPLOY_CONTRACT("deploycontract", DeployContractServlet.class,
      Access.BUILD, Surface.FULL),

  TRIGGER_SMART_CONTRACT("triggersmartcontract", TriggerSmartContractServlet.class,
      Access.BUILD, Surface.FULL),

  // constant VM call: read-only (never committed); the unsigned tx in the response is an echo
  TRIGGER_CONSTANT_CONTRACT("triggerconstantcontract", TriggerConstantContractServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  // constant VM call: read-only (never committed); the unsigned tx in the response is an echo
  ESTIMATE_ENERGY("estimateenergy", EstimateEnergyServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_CONTRACT("getcontract", GetContractServlet.class,
      Access.READ, Surface.FULL),

  GET_CONTRACT_INFO("getcontractinfo", GetContractInfoServlet.class,
      Access.READ, Surface.FULL),

  CLEAR_ABI("clearabi", ClearABIServlet.class,
      Access.BUILD, Surface.FULL),

  PROPOSAL_CREATE("proposalcreate", ProposalCreateServlet.class,
      Access.BUILD, Surface.FULL),

  PROPOSAL_APPROVE("proposalapprove", ProposalApproveServlet.class,
      Access.BUILD, Surface.FULL),

  PROPOSAL_DELETE("proposaldelete", ProposalDeleteServlet.class,
      Access.BUILD, Surface.FULL),

  LIST_PROPOSALS("listproposals", ListProposalsServlet.class,
      Access.READ, Surface.FULL),

  GET_PROPOSAL_BY_ID("getproposalbyid", GetProposalByIdServlet.class,
      Access.READ, Surface.FULL),

  EXCHANGE_CREATE("exchangecreate", ExchangeCreateServlet.class,
      Access.BUILD, Surface.FULL),

  EXCHANGE_INJECT("exchangeinject", ExchangeInjectServlet.class,
      Access.BUILD, Surface.FULL),

  EXCHANGE_TRANSACTION("exchangetransaction", ExchangeTransactionServlet.class,
      Access.BUILD, Surface.FULL),

  EXCHANGE_WITHDRAW("exchangewithdraw", ExchangeWithdrawServlet.class,
      Access.BUILD, Surface.FULL),

  GET_EXCHANGE_BY_ID("getexchangebyid", GetExchangeByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  LIST_EXCHANGES("listexchanges", ListExchangesServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_CHAIN_PARAMETERS("getchainparameters", GetChainParametersServlet.class,
      Access.READ, Surface.FULL),

  GET_ACCOUNT_RESOURCE("getaccountresource", GetAccountResourceServlet.class,
      Access.READ, Surface.FULL),

  GET_TRANSACTION_SIGN_WEIGHT("getsignweight", GetTransactionSignWeightServlet.class,
      Access.READ, Surface.FULL),

  GET_TRANSACTION_APPROVED_LIST("getapprovedlist", GetTransactionApprovedListServlet.class,
      Access.READ, Surface.FULL),

  ACCOUNT_PERMISSION_UPDATE("accountpermissionupdate", AccountPermissionUpdateServlet.class,
      Access.BUILD, Surface.FULL),

  GET_NODE_INFO("getnodeinfo", GetNodeInfoServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  UPDATE_SETTING("updatesetting", UpdateSettingServlet.class,
      Access.BUILD, Surface.FULL),

  UPDATE_ENERGY_LIMIT("updateenergylimit", UpdateEnergyLimitServlet.class,
      Access.BUILD, Surface.FULL),

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

  SET_ACCOUNT_ID("setaccountid", SetAccountIdServlet.class,
      Access.BUILD, Surface.FULL),

  GET_ACCOUNT_BY_ID("getaccountbyid", GetAccountByIdServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_EXPANDED_SPENDING_KEY("getexpandedspendingkey", GetExpandedSpendingKeyServlet.class,
      Access.READ, Surface.FULL),

  GET_AK_FROM_ASK("getakfromask", GetAkFromAskServlet.class,
      Access.READ, Surface.FULL),

  GET_NK_FROM_NSK("getnkfromnsk", GetNkFromNskServlet.class,
      Access.READ, Surface.FULL),

  GET_SPENDING_KEY("getspendingkey", GetSpendingKeyServlet.class,
      Access.READ, Surface.FULL),

  GET_NEW_SHIELDED_ADDRESS("getnewshieldedaddress", GetNewShieldedAddressServlet.class,
      Access.READ, Surface.FULL),

  GET_DIVERSIFIER("getdiversifier", GetDiversifierServlet.class,
      Access.READ, Surface.FULL),

  GET_INCOMING_VIEWING_KEY("getincomingviewingkey", GetIncomingViewingKeyServlet.class,
      Access.READ, Surface.FULL),

  GET_ZEN_PAYMENT_ADDRESS("getzenpaymentaddress", GetZenPaymentAddressServlet.class,
      Access.READ, Surface.FULL),

  GET_RCM("getrcm", GetRcmServlet.class,
      Access.READ, Surface.FULL),

  // shielded crypto material generation (no transaction is composed): read-only
  CREATE_SPEND_AUTH_SIG("createspendauthsig", CreateSpendAuthSigServlet.class,
      Access.READ, Surface.FULL),

  IS_SHIELDED_TRC20_CONTRACT_NOTE_SPENT(
      "isshieldedtrc20contractnotespent",
      IsShieldedTRC20ContractNoteSpentServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  CREATE_SHIELDED_CONTRACT_PARAMETERS(
      "createshieldedcontractparameters",
      CreateShieldedContractParametersServlet.class,
      Access.READ, Surface.FULL),

  CREATE_SHIELDED_CONTRACT_PARAMETERS_WITHOUT_ASK(
      "createshieldedcontractparameterswithoutask",
      CreateShieldedContractParametersWithoutAskServlet.class,
      Access.READ, Surface.FULL),

  SCAN_SHIELDED_TRC20_NOTES_BY_IVK(
      "scanshieldedtrc20notesbyivk",
      ScanShieldedTRC20NotesByIvkServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  SCAN_SHIELDED_TRC20_NOTES_BY_OVK(
      "scanshieldedtrc20notesbyovk",
      ScanShieldedTRC20NotesByOvkServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_TRIGGER_INPUT_FOR_SHIELDED_TRC20_CONTRACT(
      "gettriggerinputforshieldedtrc20contract",
      GetTriggerInputForShieldedTRC20ContractServlet.class,
      Access.READ, Surface.FULL),

  BROADCAST_HEX("broadcasthex", BroadcastHexServlet.class,
      Access.WRITE, Surface.FULL),

  GET_BROKERAGE("getBrokerage", GetBrokerageServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_REWARD("getReward", GetRewardServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  UPDATE_BROKERAGE("updateBrokerage", UpdateBrokerageServlet.class,
      Access.BUILD, Surface.FULL),

  CREATE_COMMON_TRANSACTION("createCommonTransaction", CreateCommonTransactionServlet.class,
      Access.BUILD, Surface.FULL),

  GET_TRANSACTION_INFO_BY_BLOCK_NUM(
      "gettransactioninfobyblocknum",
      GetTransactionInfoByBlockNumServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.SOLIDITY_NODE),

  MARKET_SELL_ASSET("marketsellasset", MarketSellAssetServlet.class,
      Access.BUILD, Surface.FULL),

  MARKET_CANCEL_ORDER("marketcancelorder", MarketCancelOrderServlet.class,
      Access.BUILD, Surface.FULL),

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

  GET_ACCOUNT_BALANCE("getaccountbalance", GetAccountBalanceServlet.class,
      Access.READ, Surface.FULL),

  GET_BLOCK_BALANCE("getblockbalance", GetBlockBalanceServlet.class,
      Access.READ, Surface.FULL),

  GET_BURN_TRX("getburntrx", GetBurnTrxServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_TRANSACTION_FROM_PENDING("gettransactionfrompending", GetTransactionFromPendingServlet.class,
      Access.READ, Surface.FULL),

  GET_TRANSACTION_LIST_FROM_PENDING(
      "gettransactionlistfrompending",
      GetTransactionListFromPendingServlet.class,
      Access.READ, Surface.FULL),

  GET_PENDING_SIZE("getpendingsize", GetPendingSizeServlet.class,
      Access.READ, Surface.FULL),

  GET_ENERGY_PRICES("getenergyprices", GetEnergyPricesServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BANDWIDTH_PRICES("getbandwidthprices", GetBandwidthPricesServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_BLOCK("getblock", GetBlockServlet.class,
      Access.READ, Surface.FULL, Surface.SOLIDITY, Surface.PBFT, Surface.SOLIDITY_NODE),

  GET_MEMO_FEE_PRICES("getmemofee", GetMemoFeePricesServlet.class,
      Access.READ, Surface.FULL),

  FREEZE_BALANCE_V2("freezebalancev2", FreezeBalanceV2Servlet.class,
      Access.BUILD, Surface.FULL),

  UN_FREEZE_BALANCE_V2("unfreezebalancev2", UnFreezeBalanceV2Servlet.class,
      Access.BUILD, Surface.FULL),

  WITHDRAW_EXPIRE_UNFREEZE("withdrawexpireunfreeze", WithdrawExpireUnfreezeServlet.class,
      Access.BUILD, Surface.FULL),

  DELEGATE_RESOURCE("delegateresource", DelegateResourceServlet.class,
      Access.BUILD, Surface.FULL),

  UN_DELEGATE_RESOURCE("undelegateresource", UnDelegateResourceServlet.class,
      Access.BUILD, Surface.FULL),

  CANCEL_ALL_UNFREEZE_V2("cancelallunfreezev2", CancelAllUnfreezeV2Servlet.class,
      Access.BUILD, Surface.FULL);

  /** Http services an endpoint can be exposed on. */
  public enum Surface {
    FULL, SOLIDITY, PBFT, SOLIDITY_NODE
  }

  /**
   * READ: no state mutation — queries, crypto derivation and constant VM calls.
   * BUILD: composes an unsigned transaction for the client to sign.
   * WRITE: pushes a transaction into the chain.
   */
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
