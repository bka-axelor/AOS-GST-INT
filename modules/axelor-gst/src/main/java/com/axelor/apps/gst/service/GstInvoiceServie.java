package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import java.math.BigDecimal;

public interface GstInvoiceServie {
  public BigDecimal calculateAllNetGst(Invoice invoice, Boolean isState);
  public Boolean compareState(Invoice invoice);
}
