package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.app.gst.purchesorder.service.GstPurcherOrderInvoiceServiceImpl;
import com.axelor.app.gst.salebatch.service.SaleBatchServiceImpl;
import com.axelor.app.gst.saleorder.service.GstSaleOrderInvoiceServiceImpl;
import com.axelor.app.gst.stockmoves.service.GstStockMoveInvoiceService;
import com.axelor.apps.account.service.AccountManagementServiceAccountImpl;
import com.axelor.apps.account.service.batch.BatchStrategy;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.businessproject.service.ProjectStockMoveInvoiceServiceImpl;
import com.axelor.apps.businessproject.service.PurchaseOrderInvoiceProjectServiceImpl;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.gst.accountmanagement.GstSetTaxLine;
import com.axelor.apps.gst.service.GstInvoiceLineService;
import com.axelor.apps.gst.service.GstInvoiceLineServiceImpl;
import com.axelor.apps.gst.service.GstInvoiceServiceImpl;
import com.axelor.apps.gst.service.GstInvoiceServie;

public class GstModule extends AxelorModule {
  @Override
  protected void configure() {
    bind(GstInvoiceServie.class).to(GstInvoiceServiceImpl.class);
    bind(GstInvoiceLineService.class).to(GstInvoiceLineServiceImpl.class);
    bind(InvoiceLineProjectServiceImpl.class).to(GstInvoiceLineServiceImpl.class);
    bind(InvoiceServiceManagementImpl.class).to(GstInvoiceServiceImpl.class);
    bind(AccountManagementServiceAccountImpl.class).to(GstSetTaxLine.class);
    bind(SaleOrderInvoiceProjectServiceImpl.class).to(GstSaleOrderInvoiceServiceImpl.class);
    bind(PurchaseOrderInvoiceProjectServiceImpl.class).to(GstPurcherOrderInvoiceServiceImpl.class);
    bind(ProjectStockMoveInvoiceServiceImpl.class).to(GstStockMoveInvoiceService.class);
    bind(BatchStrategy.class).to(SaleBatchServiceImpl.class);
  }
}
