package com.ouz;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;

public class ProductRowSetListener implements RowSetListener {

  @Override
  public void rowSetChanged(RowSetEvent event) {
    System.out.println("RowSet Changed");
  }

  @Override
  public void rowChanged(RowSetEvent event) {
    System.out.println("Row Changed");
  }

  @Override
  public void cursorMoved(RowSetEvent event) {
    System.out.println("Cursor Moved");
  }
}
