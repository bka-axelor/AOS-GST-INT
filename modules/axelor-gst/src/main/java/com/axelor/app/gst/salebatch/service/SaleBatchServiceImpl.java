package com.axelor.app.gst.salebatch.service;

import com.axelor.apps.account.service.batch.BatchStrategy;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.Template;
import com.axelor.apps.message.db.repo.TemplateRepository;
import com.axelor.apps.message.service.MessageService;
import com.axelor.apps.message.service.TemplateMessageService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;

public class SaleBatchServiceImpl extends BatchStrategy{

	@Override
	protected void process() {
		SaleOrder[] saleOrders = null;
		for (SaleOrder saleOrder : saleOrders) {
			try {
				Template template = Beans.get(TemplateRepository.class)
						.findByName("SaleOrderReport");
				Message message = Beans.get(TemplateMessageService.class).generateMessage(saleOrder,
						template);
				Beans.get(MessageService.class).sendMessage(message);
				incrementDone();
			} catch (AxelorException e) {
				incrementAnomaly();
			} catch (Exception e) {
				incrementAnomaly();
			}
		}
		
	}

}
