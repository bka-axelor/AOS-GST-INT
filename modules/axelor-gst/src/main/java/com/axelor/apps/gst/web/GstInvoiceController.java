package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.GstInvoiceLineServiceImpl;
import com.axelor.apps.gst.service.GstInvoiceServiceImpl;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.List;

public class GstInvoiceController {

  @Transactional
  public void updateInvoiceLine(ActionRequest request, ActionResponse response)
      throws AxelorException {
    Invoice invoice = request.getContext().asType(Invoice.class);
    if (Beans.get(AppSupplychainService.class).isApp("gst")
        && invoice.getPartner() != null
        && invoice.getCompany().getAddress().getState() != null
        && invoice.getAddress().getState() != null
        && invoice.getInvoiceLineList() != null) {

      String companyState = invoice.getCompany().getAddress().getState().getName();
      String partnerState = invoice.getAddress().getState().getName();
      List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
      for (InvoiceLine invoiceLine : invoiceLineList) {
        if (partnerState != null) {
          BigDecimal cgstAndSgst =
              Beans.get(GstInvoiceLineServiceImpl.class).calculateCandSgst(invoiceLine);
          invoiceLine.setSgst(cgstAndSgst);
          invoiceLine.setCgst(cgstAndSgst);
          invoiceLine.setIgst(BigDecimal.ZERO);

        } else {
          BigDecimal igst = Beans.get(GstInvoiceLineServiceImpl.class).calculateIgst(invoiceLine);
          invoiceLine.setIgst(igst);
          invoiceLine.setCgst(BigDecimal.ZERO);
          invoiceLine.setSgst(BigDecimal.ZERO);
        }
      }
      response.setValue("invoiceLineList", invoiceLineList);
      Invoice compute = Beans.get(GstInvoiceServiceImpl.class).compute(invoice);
      response.setValue("netCgst", compute.getNetCgst());
      response.setValue("netSgst", compute.getNetCgst());
      response.setValue("netIgst", compute.getNetIgst());
    } else {
      response.setValue("netCgst", BigDecimal.ZERO);
      response.setValue("netSgst", BigDecimal.ZERO);
      response.setValue("netIgst", BigDecimal.ZERO);
    }
  }
}
