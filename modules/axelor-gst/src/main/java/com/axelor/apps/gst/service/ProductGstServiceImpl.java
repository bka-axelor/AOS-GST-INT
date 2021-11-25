package com.axelor.apps.gst.service;

import com.axelor.apps.base.db.Product;
import java.math.BigDecimal;

public class ProductGstServiceImpl implements ProductGstService {

  @Override
  public BigDecimal setGstRate(Product product) {
    BigDecimal gstRate = product.getProductCategory().getGstRate();
    return gstRate;
  }
}
