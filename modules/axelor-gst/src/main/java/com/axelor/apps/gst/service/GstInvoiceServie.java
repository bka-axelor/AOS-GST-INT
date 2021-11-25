package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import java.math.BigDecimal;

public interface GstInvoiceServie {
  public BigDecimal calculateNetCandSgst(Invoice invoice);

  public BigDecimal calculateIgst(Invoice invoice);
}
