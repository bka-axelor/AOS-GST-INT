package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceLineTaxRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.account.service.move.MoveToolService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.cash.management.service.InvoiceEstimatedPaymentService;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class GstInvoiceServiceImpl extends InvoiceServiceManagementImpl implements GstInvoiceServie {
	@Inject
	InvoiceLineTaxRepository invoiceLineTaxRepo;

	@Inject
	public GstInvoiceServiceImpl(ValidateFactory validateFactory, VentilateFactory ventilateFactory,
			CancelFactory cancelFactory, AlarmEngineService<Invoice> alarmEngineService, InvoiceRepository invoiceRepo,
			AppAccountService appAccountService, PartnerService partnerService, InvoiceLineService invoiceLineService,
			AccountConfigService accountConfigService, MoveToolService moveToolService,
			InvoiceLineRepository invoiceLineRepo, InvoiceEstimatedPaymentService invoiceEstimatedPaymentService) {
		super(validateFactory, ventilateFactory, cancelFactory, alarmEngineService, invoiceRepo, appAccountService,
				partnerService, invoiceLineService, accountConfigService, moveToolService, invoiceLineRepo,
				invoiceEstimatedPaymentService);
	}

	@Override
	public Boolean compareState(Invoice invoice) {
		Boolean isState = invoice.getCompany().getAddress().getState().getName()
				.equalsIgnoreCase(invoice.getAddress().getState().getName()) ? true : false;
		return isState;
	}

	@Override
	public BigDecimal calculateAllNetGst(Invoice invoice, Boolean isState) {
		BigDecimal netGstValue = BigDecimal.ZERO;
		List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
		isState = compareState(invoice);
		for (InvoiceLine invoiceLine : invoiceLineList) {
			if (isState) {
				netGstValue = netGstValue.add(invoiceLine.getCgst());
			} else {
				netGstValue = netGstValue.add(invoiceLine.getIgst());
			}
		}
		System.err.println(netGstValue);
		return netGstValue;
	}

	@Override
	public Invoice compute(Invoice invoice) throws AxelorException {

		Invoice computeGst = super.compute(invoice);
		Boolean isState = compareState(invoice);
		BigDecimal netGstValue = calculateAllNetGst(invoice, isState);
		if (isState) {
			computeGst.setNetCgst(netGstValue);
			computeGst.setNetSgst(netGstValue);
			computeGst.setNetIgst(BigDecimal.ZERO);
		} else {
			computeGst.setNetIgst(netGstValue);
			computeGst.setNetCgst(BigDecimal.ZERO);
			computeGst.setNetSgst(BigDecimal.ZERO);
		}
		return computeGst;
	}
}
