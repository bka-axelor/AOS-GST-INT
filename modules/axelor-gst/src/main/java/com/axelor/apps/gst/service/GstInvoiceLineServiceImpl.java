package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.ProductCompanyService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;

public class GstInvoiceLineServiceImpl extends InvoiceLineProjectServiceImpl
    implements GstInvoiceLineService {
  @Inject TaxRepository taxRepo;
  @Inject TaxLineRepository taxLineRepo;

  @Inject
  public GstInvoiceLineServiceImpl(
      CurrencyService currencyService,
      PriceListService priceListService,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService,
      PurchaseProductService purchaseProductService,
      ProductCompanyService productCompanyService,
      InvoiceLineRepository invoiceLineRepo,
      AppBaseService appBaseService) {
    super(
        currencyService,
        priceListService,
        appAccountService,
        analyticMoveLineService,
        accountManagementAccountService,
        purchaseProductService,
        productCompanyService,
        invoiceLineRepo,
        appBaseService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public BigDecimal getGstRate(InvoiceLine invoiceLine) {
    Product product = invoiceLine.getProduct();
    BigDecimal gstrate = product.getGstRate();
    return gstrate;
  }

  @Override
  public BigDecimal calculateCandSgst(InvoiceLine invoiceLine) {
    BigDecimal cAndSgst = BigDecimal.ZERO;
    BigDecimal salePrice = invoiceLine.getProduct().getSalePrice();
    BigDecimal qty = invoiceLine.getQty();
    BigDecimal priceWithoutTax = salePrice.multiply(qty);
    BigDecimal gstRate = invoiceLine.getProduct().getGstRate();
    cAndSgst = priceWithoutTax.multiply(gstRate).divide(BigDecimal.valueOf(2));
    return cAndSgst;
  }

  @Override
  public BigDecimal calculateIgst(InvoiceLine invoiceLine) {
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal salePrice = invoiceLine.getProduct().getSalePrice();
    BigDecimal qty = invoiceLine.getQty();
    BigDecimal priceWithoutTax = salePrice.multiply(qty);
    BigDecimal gstRate = invoiceLine.getProduct().getGstRate();
    igst = priceWithoutTax.multiply(gstRate);
    return igst;
  }

  @Override
  public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine)
      throws AxelorException {
    Map<String, Object> productinfo = super.fillProductInformation(invoice, invoiceLine);

    BigDecimal gstRate = getGstRate(invoiceLine);
    BigDecimal setCandSgst = calculateCandSgst(invoiceLine);
    BigDecimal setIgst = calculateIgst(invoiceLine);
    productinfo.put("gstRate", gstRate);
    try {
      String companyState = invoice.getCompany().getAddress().getState().getName();
      String partnerState = invoice.getAddress().getState().getName();
      if (companyState.equals(partnerState)) {
        productinfo.put("cgst", setCandSgst);
        productinfo.put("sgst", setCandSgst);
        productinfo.put("igst", BigDecimal.ZERO);
      } else {
        productinfo.put("igst", setIgst);
        productinfo.put("cgst", BigDecimal.ZERO);
        productinfo.put("sgst", BigDecimal.ZERO);
      }
    } catch (Exception e) {

    }

    return productinfo;
  }

  @Override
  public TaxLine getTaxLine(Invoice invoice, InvoiceLine invoiceLine, boolean isPurchase)
      throws AxelorException {
    if (Beans.get(AppSupplychainService.class).isApp("gst")) {
      TaxLine taxLine =
          taxLineRepo
              .all()
              .filter(
                  "self.tax.code = 'G_ST' and self.value = ?",
                  invoiceLine.getProduct().getGstRate())
              .fetchOne();
      System.err.println(taxLine);

      return super.getTaxLine(invoice, invoiceLine, isPurchase);
    } else {
      return super.getTaxLine(invoice, invoiceLine, isPurchase);
    }
  }
}
