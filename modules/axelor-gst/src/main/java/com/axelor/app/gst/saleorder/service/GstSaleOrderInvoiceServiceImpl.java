package com.axelor.app.gst.saleorder.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.app.AppService;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.businessproject.service.app.AppBusinessProjectService;
import com.axelor.apps.gst.service.GstInvoiceLineService;
import com.axelor.apps.gst.service.GstInvoiceServie;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderWorkflowServiceImpl;
import com.axelor.apps.stock.db.repo.StockMoveRepository;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class GstSaleOrderInvoiceServiceImpl extends SaleOrderInvoiceProjectServiceImpl {

	@Inject
	GstInvoiceServie gstInvoiceService;
	@Inject
	GstInvoiceLineService gstInvoiceLineService;

	@Inject
	public GstSaleOrderInvoiceServiceImpl(AppBaseService appBaseService, AppSupplychainService appSupplychainService,
			SaleOrderRepository saleOrderRepo, InvoiceRepository invoiceRepo, InvoiceService invoiceService,
			AppBusinessProjectService appBusinessProjectService, StockMoveRepository stockMoveRepository,
			SaleOrderLineService saleOrderLineService, SaleOrderWorkflowServiceImpl saleOrderWorkflowServiceImpl) {
		super(appBaseService, appSupplychainService, saleOrderRepo, invoiceRepo, invoiceService,
				appBusinessProjectService, stockMoveRepository, saleOrderLineService, saleOrderWorkflowServiceImpl);
	}

	@Override
	public Invoice createInvoice(SaleOrder saleOrder, List<SaleOrderLine> saleOrderLineList,
			Map<Long, BigDecimal> qtyToInvoiceMap) throws AxelorException {
		
		Invoice invoice = super.createInvoice(saleOrder, saleOrderLineList, qtyToInvoiceMap);
		if(Beans.get(AppService.class).isApp("gst") && invoice.getCompany().getAddress().getState() !=null && invoice.getAddress().getState() !=null) {
			Boolean isState = gstInvoiceService.compareState(invoice);
			List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
			for (InvoiceLine invoiceLine : invoiceLineList) {
				invoiceLine.setGstRate(invoiceLine.getProduct().getGstRate());
				if (isState) {
					invoiceLine.setSgst(gstInvoiceLineService.callculateAllGgst(invoiceLine, invoice));
					invoiceLine.setCgst(gstInvoiceLineService.callculateAllGgst(invoiceLine, invoice));
					invoice.setNetCgst(gstInvoiceService.calculateAllNetGst(invoice, isState));
					invoice.setNetSgst(gstInvoiceService.calculateAllNetGst(invoice, isState));
					invoiceLine.setIgst(BigDecimal.ZERO);
				} else {
					invoiceLine.setIgst(gstInvoiceLineService.callculateAllGgst(invoiceLine, invoice));
					invoice.setNetIgst(gstInvoiceService.calculateAllNetGst(invoice, isState));
					invoiceLine.setSgst(BigDecimal.ZERO);
					invoiceLine.setCgst(BigDecimal.ZERO);
				}
			}
			invoice.setInvoiceLineList(invoiceLineList);
		}		
		return invoice;
		
		
	}
}
