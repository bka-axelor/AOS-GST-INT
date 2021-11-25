package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceLineTaxRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.account.service.move.MoveToolService;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.cash.management.service.InvoiceEstimatedPaymentService;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class GstInvoiceServiceImpl extends InvoiceServiceManagementImpl
    implements GstInvoiceServie {
  @Inject InvoiceLineTaxRepository invoiceLineTaxRepo;

  @Inject
  public GstInvoiceServiceImpl(
      ValidateFactory validateFactory,
      VentilateFactory ventilateFactory,
      CancelFactory cancelFactory,
      AlarmEngineService<Invoice> alarmEngineService,
      InvoiceRepository invoiceRepo,
      AppAccountService appAccountService,
      PartnerService partnerService,
      InvoiceLineService invoiceLineService,
      AccountConfigService accountConfigService,
      MoveToolService moveToolService,
      InvoiceLineRepository invoiceLineRepo,
      InvoiceEstimatedPaymentService invoiceEstimatedPaymentService) {
    super(
        validateFactory,
        ventilateFactory,
        cancelFactory,
        alarmEngineService,
        invoiceRepo,
        appAccountService,
        partnerService,
        invoiceLineService,
        accountConfigService,
        moveToolService,
        invoiceLineRepo,
        invoiceEstimatedPaymentService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public BigDecimal calculateNetCandSgst(Invoice invoice) {
    BigDecimal netSgstAndCgst = BigDecimal.ZERO;
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    for (InvoiceLine invoiceLine : invoiceLineList) {
      BigDecimal sAndCgst = invoiceLine.getSgst();
      netSgstAndCgst = netSgstAndCgst.add(sAndCgst);
    }
    return netSgstAndCgst;
  }

  @Override
  public BigDecimal calculateIgst(Invoice invoice) {
    BigDecimal netIgst = BigDecimal.ZERO;
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    for (InvoiceLine invoiceLine : invoiceLineList) {
      BigDecimal igst = invoiceLine.getIgst();
      netIgst = netIgst.add(igst);
    }
    return netIgst;
  }

  @Override
  public Invoice compute(Invoice invoice) throws AxelorException {

    Invoice computeGst = super.compute(invoice);
    String companyState = invoice.getCompany().getAddress().getState().getName();
    String partnerState = "";

    List<PartnerAddress> partnerAddressList = invoice.getPartner().getPartnerAddressList();
    for (PartnerAddress partnerAddress : partnerAddressList) {
      partnerState = partnerAddress.getAddress().getState().getName();
    }
    if (companyState.equals(partnerState)) {
      BigDecimal netGgst = calculateNetCandSgst(invoice);
      computeGst.setNetCgst(netGgst);
      computeGst.setNetSgst(netGgst);
      computeGst.setNetIgst(BigDecimal.ZERO);
    } else {
      BigDecimal netIgst = calculateIgst(invoice);
      computeGst.setNetIgst(netIgst);
      computeGst.setNetSgst(BigDecimal.ZERO);
      computeGst.setNetCgst(BigDecimal.ZERO);
    }
    return computeGst;
  }
}
