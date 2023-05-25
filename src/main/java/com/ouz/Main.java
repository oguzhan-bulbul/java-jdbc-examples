package com.ouz;

import com.ouz.entity.Product;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
  private static void printResult(List<Product> products) {
    products.forEach(
        product -> {
          System.out.println("Product Id = " + product.getProductId());
          System.out.println("Product Name = " + product.getProductName());
          System.out.println("Supplier Id = " + product.getSupplierId());
          System.out.println("Category Id = " + product.getCategoryId());
          System.out.println("Quantity Per Unit = " + product.getQuantityPerUnit());
          System.out.println("Unit Price = " + product.getUnitPrice());
          System.out.println("Units On Order = " + product.getUnitsOnOrder());
          System.out.println("Units In Stock = " + product.getUnitsInStock());
          System.out.println("Reorder level = " + product.getReOrderLevel());
          System.out.println("Discontinued = " + product.getDisContinued());
          System.out.println("==============================================");
        });
  }

  public static void main(String[] args) throws SQLException {
    ProductDaoJavaJDBC productService = new ProductDaoJavaJDBC();
//        List<Product> allProducts = productService.getAllProducts();
//        printResult(allProducts);

//        List<Product> productsBySupplierId = productService.getProductsBySupplierId(8);
//        printResult(productsBySupplierId);

//        int effectedRowNumber = productService.updateProductNameByProductId("Trabzonspor Formasi", 1);
//        System.out.println(effectedRowNumber);

    List<Product> productsByCategoryId = productService.getProductsByCategoryId(2);
//    printResult(productsByCategoryId);
//
    productService.updateProductPrice(productsByCategoryId,1.5f,50);

//    List<Product> productsByCategoryId = productService.getProductsByCategoryId(7);
//    printResult(productsByCategoryId);

//    int ordersBetweenDates =
//                productService.getOrdersBetweenDates(
//                    Date.valueOf("1996-07-08"), Date.valueOf("1996-07-25"));
//            System.out.println(ordersBetweenDates);


//        Map<Integer, String> updateProductsMap = new HashMap<>();
//
//        updateProductsMap.put(1, "Name1");
//        updateProductsMap.put(2, "Name2");
//        updateProductsMap.put(3, "Name3");
//        updateProductsMap.put(4, "Name4");
//        updateProductsMap.put(5, "Name5");
//        updateProductsMap.put(6, "Name6");
//        updateProductsMap.put(7, "Name7");
//        updateProductsMap.put(8, "Name8");
//
//        int[] ints = productService.updateProductNameByIdWithBatch(updateProductsMap);
////        for (int anInt : ints) {
////          System.out.println(anInt);
////        }
//
//        for (Entry<Integer, String> entry : updateProductsMap.entrySet()) {
//          productService.updateProductNameByProductId(entry.getValue(), entry.getKey());
//        }

    //    productService.doExercisesOnProductWithJdbcRowSet();
//    productService.doExercisesOnProductWithCachedRowSet();
//    productService.doExercisesOnProductWithJoinRowSet();
//    productService.doExercisesOnProductWithFilteredRowSet();
//    productService.doExercisesOnProductWithFWebRowSet();

  }
}
