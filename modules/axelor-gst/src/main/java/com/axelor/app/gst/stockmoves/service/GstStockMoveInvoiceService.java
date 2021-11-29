package com.axelor.app.gst.stockmoves.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.businessproject.service.ProjectStockMoveInvoiceServiceImpl;
import com.axelor.apps.gst.service.GstInvoiceLineService;
import com.axelor.apps.gst.service.GstInvoiceServie;
import com.axelor.apps.purchase.db.repo.PurchaseOrderRepository;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.stock.db.StockMove;
import com.axelor.apps.stock.db.repo.StockMoveLineRepository;
import com.axelor.apps.supplychain.service.PurchaseOrderInvoiceService;
import com.axelor.apps.supplychain.service.SaleOrderInvoiceService;
import com.axelor.apps.supplychain.service.StockMoveLineServiceSupplychain;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.apps.supplychain.service.config.SupplyChainConfigService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class GstStockMoveInvoiceService extends ProjectStockMoveInvoiceServiceImpl{
	
	@Inject
	GstInvoiceServie gstInvoiceService;
	@Inject
	GstInvoiceLineService gstInvoiceLineService;
	
	@Inject
	public GstStockMoveInvoiceService(SaleOrderInvoiceService saleOrderInvoiceService,
			PurchaseOrderInvoiceService purchaseOrderInvoiceService,
			StockMoveLineServiceSupplychain stockMoveLineServiceSupplychain, InvoiceRepository invoiceRepository,
			SaleOrderRepository saleOrderRepo, PurchaseOrderRepository purchaseOrderRepo,
			StockMoveLineRepository stockMoveLineRepository, InvoiceLineRepository invoiceLineRepository,
			SupplyChainConfigService supplyChainConfigService, AppSupplychainService appSupplychainService) {
		super(saleOrderInvoiceService, purchaseOrderInvoiceService, stockMoveLineServiceSupplychain, invoiceRepository,
				saleOrderRepo, purchaseOrderRepo, stockMoveLineRepository, invoiceLineRepository, supplyChainConfigService,
				appSupplychainService);
	}
	
	@Override
	@Transactional(rollbackOn = { Exception.class })
	public Invoice createInvoice(StockMove stockMove, Integer operationSelect,
			List<Map<String, Object>> stockMoveLineListContext) throws AxelorException {
		Invoice invoice = super.createInvoice(stockMove, operationSelect, stockMoveLineListContext);
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
			return invoice;
		}else {
			return super.createInvoice(stockMove, operationSelect, stockMoveLineListContext);
		}
		
		}
	
	
}
