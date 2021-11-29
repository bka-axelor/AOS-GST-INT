package com.axelor.apps.gst.web;

import com.axelor.apps.base.db.Product;
import com.axelor.apps.gst.service.ProductGstServiceImpl;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.math.BigDecimal;

public class ProductController {

  public void setGstRate(ActionRequest request, ActionResponse response) {
    Product product = request.getContext().asType(Product.class);
    if(product.getProductCategory() != null) {
    	BigDecimal setGstRate = Beans.get(ProductGstServiceImpl.class).setGstRate(product).divide(BigDecimal.valueOf(100));
    	response.setValue("gstRate", setGstRate);
    }else {
    	response.setValue("gstRate", BigDecimal.ZERO);
    }
    
  }
}
