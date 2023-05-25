package com.ouz.entity;

public class Product {
  private int productId;
  private String productName;
  private int supplierId;
  private int categoryId;
  private String quantityPerUnit;
  private int unitPrice;
  private int unitsInStock;
  private int unitsOnOrder;
  private int reOrderLevel;
  private int disContinued;

  public Product() {}

  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public int getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(int supplierId) {
    this.supplierId = supplierId;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }

  public String getQuantityPerUnit() {
    return quantityPerUnit;
  }

  public void setQuantityPerUnit(String quantityPerUnit) {
    this.quantityPerUnit = quantityPerUnit;
  }

  public int getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(int unitPrice) {
    this.unitPrice = unitPrice;
  }

  public int getUnitsInStock() {
    return unitsInStock;
  }

  public void setUnitsInStock(int unitsInStock) {
    this.unitsInStock = unitsInStock;
  }

  public int getUnitsOnOrder() {
    return unitsOnOrder;
  }

  public void setUnitsOnOrder(int unitsOnOrder) {
    this.unitsOnOrder = unitsOnOrder;
  }

  public int getReOrderLevel() {
    return reOrderLevel;
  }

  public void setReOrderLevel(int reOrderLevel) {
    this.reOrderLevel = reOrderLevel;
  }

  public int getDisContinued() {
    return disContinued;
  }

  public void setDisContinued(int disContinued) {
    this.disContinued = disContinued;
  }
}
