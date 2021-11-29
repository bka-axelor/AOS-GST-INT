package com.axelor.app.gst.purchesorder.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.businessproject.service.PurchaseOrderInvoiceProjectServiceImpl;
import com.axelor.apps.gst.service.GstInvoiceLineService;
import com.axelor.apps.gst.service.GstInvoiceServie;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.List;

public class GstPurcherOrderInvoiceServiceImpl extends PurchaseOrderInvoiceProjectServiceImpl {

	@Inject
	GstInvoiceServie gstInvoiceService;
	@Inject
	GstInvoiceLineService gstInvoiceLineService;
	
	@Override
	@Transactional(rollbackOn = { Exception.class })
	public Invoice generateInvoice(PurchaseOrder purchaseOrder) throws AxelorException {
		Invoice invoice = super.generateInvoice(purchaseOrder);
		if(Beans.get(AppSupplychainService.class).isApp("gst") && invoice.getCompany().getAddress().getState() !=null && invoice.getAddress().getState() !=null) {
			Boolean isState= gstInvoiceService.compareState(invoice);
			List<InvoiceLine>invoiceLineList= invoice.getInvoiceLineList();	
			for(InvoiceLine invoiceLine: invoiceLineList) {
				invoiceLine.setGstRate(invoiceLine.getProduct().getGstRate());
				BigDecimal gstValue = gstInvoiceLineService.callculateAllGgst(invoiceLine, invoice);
				if (isState) {
					invoiceLine.setCgst(gstValue);
					invoiceLine.setSgst(gstValue);
					invoice.setNetCgst(gstInvoiceService.calculateAllNetGst(invoice, isState));
					invoice.setNetSgst(gstInvoiceService.calculateAllNetGst(invoice, isState));
				}else {
					invoiceLine.setIgst(gstValue);
					invoice.setNetIgst(gstInvoiceService.calculateAllNetGst(invoice, isState));
				}
			}
			invoice.setInvoiceLineList(invoiceLineList);		
			
		}
		return invoice;
	}

}
