package com.ouz;

import java.sql.SQLException;
import javax.sql.RowSet;
import javax.sql.rowset.Predicate;

public class CategoryFilter implements Predicate {
  private int categoryId;

  public CategoryFilter(int categoryId) {
    this.categoryId = categoryId;
  }

  @Override
  public boolean evaluate(RowSet rs) {
    try {
      if (rs.getInt("category_id") != categoryId) {
        return false;
      }
      return true;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean evaluate(Object value, int column) throws SQLException {
    if (column != 4) {
      return false;
    } else {
      int categoryIdFromTable = ((Integer) value).intValue();
      if (this.categoryId != categoryIdFromTable) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean evaluate(Object value, String columnName) throws SQLException {

    if (!columnName.equalsIgnoreCase("category_id")) {
      return false;
    } else {
      int categoryIdFromTable = ((Integer) value).intValue();
      if (this.categoryId != categoryIdFromTable) {
        return false;
      }
    }

    return true;
  }
}
