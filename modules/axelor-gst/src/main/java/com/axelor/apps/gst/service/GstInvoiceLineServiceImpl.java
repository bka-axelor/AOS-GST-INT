package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
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
import com.axelor.apps.base.service.app.AppService;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;

public class GstInvoiceLineServiceImpl extends InvoiceLineProjectServiceImpl implements GstInvoiceLineService {
	@Inject
	TaxRepository taxRepo;
	@Inject
	TaxLineRepository taxLineRepo;

	@Inject
	GstInvoiceServie gstInvoiceSer;

	@Inject
	public GstInvoiceLineServiceImpl(CurrencyService currencyService, PriceListService priceListService,
			AppAccountService appAccountService, AnalyticMoveLineService analyticMoveLineService,
			AccountManagementAccountService accountManagementAccountService,
			PurchaseProductService purchaseProductService, ProductCompanyService productCompanyService,
			InvoiceLineRepository invoiceLineRepo, AppBaseService appBaseService) {
		super(currencyService, priceListService, appAccountService, analyticMoveLineService,
				accountManagementAccountService, purchaseProductService, productCompanyService, invoiceLineRepo,
				appBaseService);
	}

	@Override
	public BigDecimal getGstRate(InvoiceLine invoiceLine) {
		Product product = invoiceLine.getProduct();
		BigDecimal gstrate = product.getGstRate();
		return gstrate;
	}

	@Override
	public BigDecimal callculateAllGgst(InvoiceLine invoiceLine, Invoice invoice) {
		BigDecimal gstValue = BigDecimal.ZERO;
		BigDecimal getExTaxTotal = invoiceLine.getQty().multiply(invoiceLine.getProduct().getSalePrice());
		Boolean isState = gstInvoiceSer.compareState(invoice);
		if (isState) {
			gstValue = getExTaxTotal.multiply(invoiceLine.getProduct().getGstRate()).divide(BigDecimal.valueOf(2));
		} else {
			gstValue = getExTaxTotal.multiply(invoiceLine.getProduct().getGstRate());
		}
		return gstValue;
	}

	@Override
	public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine) throws AxelorException {
		Map<String, Object> productinfo = super.fillProductInformation(invoice, invoiceLine);
		if (Beans.get(AppService.class).isApp("gst") && invoice.getPartner() != null) {
			BigDecimal gstRate = getGstRate(invoiceLine);
			productinfo.put("gstRate", gstRate);
			Boolean isState = gstInvoiceSer.compareState(invoice);
			BigDecimal gstValue = callculateAllGgst(invoiceLine, invoice);
			if (isState) {
				productinfo.put("cgst", gstValue);
				productinfo.put("sgst", gstValue);		
			} else {
				productinfo.put("igst", gstValue);
			}
		}
		
		return productinfo;
	}
}
