package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.InvoiceLine;
import java.math.BigDecimal;

public interface GstInvoiceLineService {
  public BigDecimal calculateCandSgst(InvoiceLine invoiceLine);

  public BigDecimal calculateIgst(InvoiceLine invoiceLine);

  public BigDecimal getGstRate(InvoiceLine invoiceLine);
}
