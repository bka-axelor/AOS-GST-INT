package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.GstInvoiceLineServiceImpl;
import com.axelor.apps.gst.service.GstInvoiceServie;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;

public class GstInvoiceLineController {

  public void qtyOnChange(ActionRequest request, ActionResponse response) {
    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    Invoice invoice = invoiceLine.getInvoice();
    if (invoice == null) {
      invoice = request.getContext().getParent().asType(Invoice.class);
    }
    if (Beans.get(AppSupplychainService.class).isApp("gst") && invoice.getPartner() != null) {
      Boolean isState = Beans.get(GstInvoiceServie.class).compareState(invoice);
      BigDecimal gstValue =
          Beans.get(GstInvoiceLineServiceImpl.class).callculateAllGgst(invoiceLine, invoice);
      if (invoiceLine.getProduct().equals(null)) {
        if (isState) {
          response.setValue("cgst", gstValue);
          response.setValue("sgst", gstValue);
        } else {
          response.setValue("igst", gstValue);
        }
      } else {
        response.setValue("igst", BigDecimal.ZERO);
        response.setValue("cgst", BigDecimal.ZERO);
        response.setValue("sgst", BigDecimal.ZERO);
      }
    }
  }
}
