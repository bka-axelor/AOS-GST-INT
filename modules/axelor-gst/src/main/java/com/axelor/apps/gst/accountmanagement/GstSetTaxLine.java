package com.axelor.apps.gst.accountmanagement;

import com.axelor.apps.account.db.FiscalPosition;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.service.AccountManagementServiceAccountImpl;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.tax.FiscalPositionService;
import com.axelor.apps.base.service.tax.TaxService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

public class GstSetTaxLine extends AccountManagementServiceAccountImpl {

  @Inject TaxLineRepository taxLineRepo;

  @Inject
  public GstSetTaxLine(FiscalPositionService fiscalPositionService, TaxService taxService) {
    super(fiscalPositionService, taxService);
  }

  @Override
  public Tax getProductTax(
      Product product, Company company, FiscalPosition fiscalPosition, boolean isPurchase)
      throws AxelorException {
    Tax tax = super.getProductTax(product, company, fiscalPosition, isPurchase);

    if (Beans.get(AppSupplychainService.class).isApp("gst")) {
      TaxLine taxLine =
          taxLineRepo
              .all()
              .filter("self.tax.code= ? and self.value= ?", "G_ST", product.getGstRate())
              .fetchOne();
      return taxLine.getTax();
    }
    return tax;
  }
}
