package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.AccountManagementServiceAccountImpl;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.gst.accountmanagement.GstSetTaxLine;
import com.axelor.apps.gst.service.GstInvoiceLineServiceImpl;
import com.axelor.apps.gst.service.GstInvoiceServiceImpl;
import com.axelor.apps.gst.service.GstInvoiceServie;

public class GstModule extends AxelorModule {
  @Override
  protected void configure() {
	  bind(GstInvoiceServie.class).to(GstInvoiceServiceImpl.class);
    bind(InvoiceLineProjectServiceImpl.class).to(GstInvoiceLineServiceImpl.class);
    bind(InvoiceServiceManagementImpl.class).to(GstInvoiceServiceImpl.class);
    bind(AccountManagementServiceAccountImpl.class).to(GstSetTaxLine.class);
  }
}
