package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.gst.service.GstInvoiceLineServiceImpl;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;
import java.util.List;

public class GstInvoiceLineController {

  public void qtyOnChange(ActionRequest request, ActionResponse response) {
    if (Beans.get(AppSupplychainService.class).isApp("gst")) {
      InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
      Invoice invoice = invoiceLine.getInvoice();
      if (invoice == null) {
        invoice = request.getContext().getParent().asType(Invoice.class);
      }

      try {
        String companyState = invoice.getCompany().getAddress().getState().getName();
        String partnerState = "";
        List<PartnerAddress> partnerAddressList = invoice.getPartner().getPartnerAddressList();
        for (PartnerAddress partnerAddress : partnerAddressList) {
          partnerState = partnerAddress.getAddress().getState().getName();
        }

        if (companyState.equals(partnerState)) {
          BigDecimal csgst =
              Beans.get(GstInvoiceLineServiceImpl.class).calculateCandSgst(invoiceLine);
          response.setValue("cgst", csgst);
          response.setValue("sgst", csgst);
        } else {
          BigDecimal igst = Beans.get(GstInvoiceLineServiceImpl.class).calculateIgst(invoiceLine);
          response.setValue("igst", igst);
        }
      } catch (Exception e) {
        response.setError("State Field Is Empty");
      }
    }
  }
}
