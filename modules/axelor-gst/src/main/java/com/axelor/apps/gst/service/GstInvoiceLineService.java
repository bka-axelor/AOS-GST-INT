package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import java.math.BigDecimal;

public interface GstInvoiceLineService {

  public BigDecimal getGstRate(InvoiceLine invoiceLine);
  
  public BigDecimal callculateAllGgst(InvoiceLine invoiceLine, Invoice invoice);
}
