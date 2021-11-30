package com.axelor.app.gst.purchesorder.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.base.service.app.AppService;
import com.axelor.apps.businessproject.service.PurchaseOrderInvoiceProjectServiceImpl;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class GstPurcherOrderInvoiceServiceImpl extends PurchaseOrderInvoiceProjectServiceImpl {

	@Inject
	InvoiceLineService invoiceLineService;
	@Inject
	InvoiceService invoiceService;
	
	@Override
	@Transactional(rollbackOn = { Exception.class })
	public Invoice generateInvoice(PurchaseOrder purchaseOrder) throws AxelorException {
		Invoice invoice = super.generateInvoice(purchaseOrder);
		if(Beans.get(AppService.class).isApp("gst") && invoice.getCompany().getAddress().getState() !=null && invoice.getAddress().getState() !=null) {
				List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
				for (InvoiceLine invoiceLine : invoiceLineList) {
					Map<String, Object> fillProductInfo=invoiceLineService.fillProductInformation(invoice, invoiceLine);
					invoiceLine.setGstRate(invoiceLine.getProduct().getGstRate());
					invoiceLine.setCgst((BigDecimal) fillProductInfo.get("cgst"));
					invoiceLine.setSgst((BigDecimal) fillProductInfo.get("sgst"));
					invoiceLine.setIgst((BigDecimal) fillProductInfo.get("igst"));
				}
				invoice.setInvoiceLineList(invoiceLineList);
				 Invoice compute = invoiceService.compute(invoice);
				 invoice.setNetCgst(compute.getNetCgst());
				 invoice.setNetSgst(compute.getNetSgst());
				 invoice.setNetIgst(compute.getNetIgst());			
		}
		return invoice;
	}

}
