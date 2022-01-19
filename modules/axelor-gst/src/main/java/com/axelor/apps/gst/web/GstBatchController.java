package com.axelor.apps.gst.web;

import com.axelor.apps.base.db.Batch;
import com.axelor.apps.sale.db.SaleBatch;
import com.axelor.apps.sale.db.repo.SaleBatchRepository;
import com.axelor.apps.supplychain.service.batch.BatchInvoicing;
import com.axelor.apps.supplychain.web.SaleBatchController;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class GstBatchController extends SaleBatchController{
	
	public void actionBatceSale(ActionRequest request, ActionResponse response) {
		SaleBatch saleBatch = request.getContext().asType(SaleBatch.class);
	    saleBatch = Beans.get(SaleBatchRepository.class).find(saleBatch.getId());
	    Batch batch = Beans.get(BatchInvoicing.class).run(saleBatch);
	    response.setFlash(batch.getComments());
	    response.setReload(true);		
	}

}
