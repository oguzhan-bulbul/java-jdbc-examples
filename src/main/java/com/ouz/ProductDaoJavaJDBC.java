package com.ouz;

import com.ouz.entity.Product;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.WebRowSet;

public class ProductDaoJavaJDBC {
  /**
   * -JDBC API'de temel olarak adimlar;
   *
   * <p>1. Connection bilgilrimiz ile Connection sinifini implemente eden bir connection olusturmak.
   *
   * <p>2.Database uzerinde execute edecegimiz statement nesnesini olusturmak.
   *
   * <p>3.Statement execute edildikten sonra donen nesneleri manipule etmek ve kullanmak.
   *
   * <p>-Java JDBC API'de temel olarak 3 adet farkli statement yapisi vardir.
   *
   * <p>1.Statement
   *
   * <p>2.PreparedStatement
   *
   * <p>3.CallableStatement
   *
   * <p>-Statement'lari execute etmek icin 3 adet method bulunur.
   *
   * <p>1. boolean execute(String sql) - Eger sorgu sonucunda bir ResultSet objesi donduyse true
   * aksi takdirde false doner. Herhangi bir sey donmesi beklenmeyen DDL ifadelerinde kullanilmasi
   * daha uygundur.
   *
   * <p>2. int executeUpdate(String sql) - Sorgudan etkilenen satir sayisini geriye doner. INSERT,
   * UPDATE ,DELETE sql statementlari icin daha uygundur.
   *
   * <p>3. ResultSet executeQuery(String sql) - ResultSet objesi doner ve bu obje uzerinden donen
   * bilgileri alip amacimiza uygun bir sekilde kullanabiliriz. SELECT statement'lari icin en uygun
   * yontemdir.
   *
   * <p>-ResultSet nesnesi database donen sonucu ifade eder.
   *
   * <p>ResultSet nesnesi uzerindeki metodlar 3 farkli kategoride incelenebilir.
   *
   * <p>1. Navigational Methods - ResultSet uzerinde gezinmemizi saglayan methodlardir.
   *
   * <p>2. Get Methods - ResultSet ile birlikte gelen sonuclari almamizi saglayan methodlardir.
   *
   * <p>3. Update Methods - ResultSet nesnesi ile birlikte gelen database kolonlarini guncellememize
   * yarar. Bu guncellemeler daha sonra database'e de yansitilabilir.
   *
   * <p>-ResultSet nesneleri ile alakali Type ve Concurrency olmak uzere 2 adet bilinmesi gereken
   * parametre vardir.
   *
   * <p>-Type Parametreleri
   *
   * <p>1.ResultSet.TYPE_FORWARD_ONLY - Default olarak bu parametre ile olusturulur. ResultSet
   * uzerinde sadece ileriye dogru gezinmeye musaade edilir.
   *
   * <p>2.ResultSet.TYPE_SCROLL_INSENSITIVE - ResultSet uzerinde hem ileriye hem geriye gezinmeye
   * musaade edilir. Ayni zamanda ResultSet olusturulduktan sonra database'de yapilan degisikliklere
   * karsi duyarsizdir.
   *
   * <p>3.ResultSet.TYPE_SCROLL_SENSITIVE - ResultSet uzerinde hem ileriye hem geriye gezinmeye
   * musaade edilir. Ayni zamanda ResultSet olusturulduktan sonra database'de yapilan degisikliklere
   * karsi duyarlidir.
   *
   * <p>-Concurrency Parametleri
   *
   * <p>1.ResultSet.CONCUR_READ_ONLY - Bu parametre ile ResultSet uzerinde sadece okuma islemi
   * yapilmasina izin verilir. Herhangi bi parametre vermezsek default olarak bu parametre ile
   * olusturulur.
   *
   * <p>2.ResultSet.CONCUR_UPDATABLE - ResultSet uzerinde hem okuma hemde update etme islemlerine
   * izin verilir.
   */
  public static String url = "jdbc:postgresql://localhost:5432/postgres";

  public static String username = "postgres";

  public static String password = "12345";

  public static String GET_ALL_PRODUCTS_SQL = "SELECT * FROM PRODUCTS";
  public static String GET_PRODUCTS_BY_SUPPLIER_ID = "SELECT * FROM PRODUCTS WHERE SUPPLIER_ID = ?";
  public static String GET_PRODUCTS_BY_CATEGORY_ID = "SELECT * FROM PRODUCTS WHERE CATEGORY_ID = ?";
  public static String UPDATE_PRODUCT_NAME_BY_ID =
      "UPDATE PRODUCTS SET PRODUCT_NAME = ? WHERE PRODUCT_ID = ?";

  /**
   * Tum Product Nesnelerini DB'den Statement kullanarak getirmek icin Connection ve Statement
   *
   * <p>nesnlerimizi olusturduk. Bu durum ResultSet default degerler ile olusturulacaktir.
   *
   * <p>ResultSet nesnesi uzerinde gezinerek get methodlari ile veriler alinir.
   *
   * <p>Burada dikkat edilmesi gereken seylerden birisi dogru veri tipine ait get methodunun
   * kullanilmasidir.
   *
   * <p>ResultSet nesnesi uzerinde bir cok veri tipi icin uygun get metodu bulunmaktadir.
   *
   * <p>Bu getMethodlari icerisine kolon ismi veya kolon indexi alir ve bu bilgiye gore veriyi
   * doner.
   *
   * <p>Kolon index'leri 0'dan degil 1'den baslar.
   *
   * <p>Connection ve Statement yapilarini kullandiktan kaynak tuketmemesi adina close etmek
   * onemlidir.
   *
   * <p>AutoCloseable olduklari icin try with resource yapisi ile kullanilabilir.
   *
   * <p>Statement yapisi verilen sql'i calistirir ve bu sql uzerinde parametrik degisiklikler
   * yapilamaz.
   *
   * <p>Bu sebepten oturu PreparedStatement kullanilir.
   *
   * <p>Veri tipi olarak gelismis veri tiplerini kullanmak mumkunkudur. CLOB,BLOB,SQLXML vb bunlar icin
   * get ve update methodlari bulunmaktadir.
   */
  public List<Product> getAllProducts() {
    List<Product> productList = new ArrayList<>();
    Statement statement = null;
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      statement = conn.createStatement();
      ResultSet resultSet = statement.executeQuery(GET_ALL_PRODUCTS_SQL);

      while (resultSet.next()) {
        Product product = new Product();
        product.setProductId(resultSet.getInt(1));
        product.setProductName(resultSet.getString(2));
        product.setSupplierId(resultSet.getInt(3));
        product.setCategoryId(resultSet.getInt(4));
        product.setQuantityPerUnit(resultSet.getString(5));
        product.setUnitPrice(resultSet.getInt(6));
        product.setUnitsInStock(resultSet.getInt(7));
        product.setUnitsOnOrder(resultSet.getInt(8));
        product.setReOrderLevel(resultSet.getInt(9));
        product.setDisContinued(resultSet.getInt(10));
        productList.add(product);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {

      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return productList;
  }

  /**
   * -PreparedStatement ifadeleri ile parametrik olarak sorgular olusturabiliriz.
   *
   * <p>Sorgumuzu olusturdugumuz String ifadesine parametrik olmasini istedigimiz yerlere ? ifadesi
   * koyariz.
   *
   * <p>Sonraki adimda preparedStatement uzerindeki setInt vb methodlar ile parametrelerimizi set
   * ederiz.
   *
   * <p>Burada set methodlari bizden parameterIndex ister. Index'ler 1 den baslar ve soldan saga ?
   * ifadelerinin sirasini ifade eder.
   */
  public List<Product> getProductsBySupplierId(int id) {
    List<Product> productList = new ArrayList<>();
    PreparedStatement preparedStatement = null;

    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      preparedStatement = conn.prepareStatement(GET_PRODUCTS_BY_SUPPLIER_ID);
      preparedStatement.setInt(1, id);

      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Product product = new Product();
        product.setProductId(resultSet.getInt(1));
        product.setProductName(resultSet.getString(2));
        product.setSupplierId(resultSet.getInt(3));
        product.setCategoryId(resultSet.getInt(4));
        product.setQuantityPerUnit(resultSet.getString(5));
        product.setUnitPrice(resultSet.getInt(6));
        product.setUnitsInStock(resultSet.getInt(7));
        product.setUnitsOnOrder(resultSet.getInt(8));
        product.setReOrderLevel(resultSet.getInt(9));
        product.setDisContinued(resultSet.getInt(10));
        productList.add(product);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (preparedStatement != null) {
        try {
          preparedStatement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    return productList;
  }

  /**
   * -PreparedStatement kullanarak bir update islemi yapilmistir.
   *
   * <p>executeUpdate() metodu kullanilmistir.
   *
   * <p>Bu metod yukaridada bahsedildigi gibi etkilenen satir sayisini doner.
   */
  public int updateProductNameByProductId(String productName, int productId) {
    int effectedRowNumber = 0;

    PreparedStatement preparedStatement = null;
    try (Connection connection = DriverManager.getConnection(url, username, password)) {

      preparedStatement = connection.prepareStatement(UPDATE_PRODUCT_NAME_BY_ID);
      preparedStatement.setString(1, productName);
      preparedStatement.setInt(2, productId);
      Instant start = Instant.now();
      effectedRowNumber = preparedStatement.executeUpdate();
      Instant finish = Instant.now();
      System.out.println("Standart : "+ Duration.between(start, finish).getNano()/1_000_000);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (preparedStatement != null) {
        try {
          preparedStatement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return effectedRowNumber;
  }

  /**
   * Batch update islemi
   *
   * @param idAndValueMapForUpdate
   * @return
   */
  public int[] updateProductNameByIdWithBatch(Map<Integer, String> idAndValueMapForUpdate) {

    PreparedStatement preparedStatement;

    try (Connection connection = DriverManager.getConnection(url, username, password)) {


      preparedStatement = connection.prepareStatement(UPDATE_PRODUCT_NAME_BY_ID);
      for (Entry<Integer, String> entry : idAndValueMapForUpdate.entrySet()) {
        preparedStatement.setString(1, entry.getValue());
        preparedStatement.setInt(2, entry.getKey());
        preparedStatement.addBatch();
      }
      Instant start = Instant.now();
      int[] ints = preparedStatement.executeBatch();
      Instant finish = Instant.now();
      System.out.println("Batch : " + Duration.between(start, finish).getNano()/1_000_000);
      return ints;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new int[0];
  }

  /**
   * Stored Procedure veya Stored Function cagrimi
   *
   * @param date1
   * @param date2
   * @return
   */
  public int getOrdersBetweenDates(Date date1, Date date2) {

    CallableStatement callableStatement = null;

    try (Connection conn = DriverManager.getConnection(url, username, password)) {

      callableStatement =
          conn.prepareCall("{ ? = call public.get_how_many_orders_between_dates(?,?)}");
      callableStatement.registerOutParameter(1, Types.INTEGER);
      callableStatement.setDate(2, date1);
      callableStatement.setDate(3, date2);

      callableStatement.execute();
      return callableStatement.getInt(1);

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }finally{
      if(callableStatement != null){
        try {
          callableStatement.close();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  /** ResultSet uzerinden update islemi yapmak ve resultSet uzerinde gezinmek */
  public List<Product> getProductsByCategoryId(int categoryId) {
    List<Product> productList = new ArrayList<>();
    PreparedStatement statement = null;
    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      statement =
          connection.prepareStatement(
              GET_PRODUCTS_BY_CATEGORY_ID,
              ResultSet.TYPE_SCROLL_INSENSITIVE,
              ResultSet.CONCUR_UPDATABLE);
      statement.setInt(1, categoryId);
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        if (resultSet.getString(2).equals("Longlife Tofu")) {
          resultSet.updateString(2,"Yeni Isim");
          resultSet.updateRow();
          resultSet.first();
        }

        Product product = new Product();
        product.setProductId(resultSet.getInt(1));
        product.setProductName(resultSet.getString(2));
        product.setSupplierId(resultSet.getInt(3));
        product.setCategoryId(resultSet.getInt(4));
        product.setQuantityPerUnit(resultSet.getString(5));
        product.setUnitPrice(resultSet.getInt(6));
        product.setUnitsInStock(resultSet.getInt(7));
        product.setUnitsOnOrder(resultSet.getInt(8));
        product.setReOrderLevel(resultSet.getInt(9));
        product.setDisContinued(resultSet.getInt(10));
        productList.add(product);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return productList;
  }

  /**
   * SavePoint kullanimi SavePoint connection uzerinde ihtiyacimiz oldugunda o noktaya donebilmek
   * icin rollback islemi yapabilmemizi saglar. SavePoint kullaniminda autoCommit parametresi false
   * olmadilir.
   */
  public void updateProductPrice(List<Product> products, float raise, int maxPrice) {
    PreparedStatement updateStatement = null;

    try (Connection con = DriverManager.getConnection(url, username, password)) {

      con.setAutoCommit(false);
      updateStatement =
          con.prepareStatement("UPDATE PRODUCTS SET unit_price = ? WHERE product_id = ?");

      for (Product product : products) {
        float newPrice = product.getUnitPrice() * raise;
        Savepoint savePoint1 = con.setSavepoint();
        updateStatement.setFloat(1, newPrice);
        updateStatement.setInt(2, product.getProductId());
        updateStatement.executeUpdate();

        if (newPrice > maxPrice) {
          con.rollback(savePoint1);
          System.out.println(
              product.getProductId()
                  + " : id'li product maxPrice sinirlamasina takildigi icin guncellenemedi.");
        } else {
          System.out.println(
              product.getProductId() + " : id'li product guncellendi. Yeni ucret : " + newPrice);
        }
      }
      con.commit();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      if (updateStatement != null) {
        try {
          updateStatement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * JdbcRowSet nesneleri gelistirilmis ResultSet objeleridir. Aradaki en buyuk fark daha gelismis
   * propertyler ekleyebiliriz ve Listener Notification mekanizmasidir.
   */
  public void doExercisesOnProductWithJdbcRowSet()  {

    RowSetFactory rowSetFactory = null;
    try {
      rowSetFactory = RowSetProvider.newFactory();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    try (JdbcRowSet jdbcRowSet = rowSetFactory.createJdbcRowSet()) {
      jdbcRowSet.setUrl(url);
      jdbcRowSet.setUsername(username);
      jdbcRowSet.setPassword(password);
      jdbcRowSet.setCommand("SELECT * FROM PRODUCTS WHERE CATEGORY_ID = 1");

      jdbcRowSet.execute();

            jdbcRowSet.absolute(4);
            System.out.println(jdbcRowSet.getInt("product_id") + "  :  " +
       jdbcRowSet.getFloat("unit_price"));
            jdbcRowSet.updateFloat("unit_price" , 22);
            jdbcRowSet.updateRow();

            jdbcRowSet.moveToInsertRow();
            jdbcRowSet.updateInt(1,98);
            jdbcRowSet.updateString(2,"YeniUrun");
            jdbcRowSet.updateInt(3,2);
            jdbcRowSet.updateInt(4,1);
            jdbcRowSet.updateString(5,"20x30");
            jdbcRowSet.updateFloat(6,61.61f);
            jdbcRowSet.updateInt(7,61);
            jdbcRowSet.updateInt(8,5);
            jdbcRowSet.updateInt(9,15);
            jdbcRowSet.updateInt(10,1);
            jdbcRowSet.insertRow();

      jdbcRowSet.last();
      System.out.println(jdbcRowSet.getInt("product_id"));
      jdbcRowSet.deleteRow();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * CachedRowSet JdbcRowSet ile farki JdbcRowSet database'e bagli birsekilde islemleri yapar.
   * CachedRowSet ise verileri alir ve cacheler. Verileri uzerinde yapacagimiz islemler Cachlelenmis
   * olan bu rowset uzerinde yapilir. Yapilan degisiklerin yansitilmasi icin acceptChanges metodu
   * kullanilmalidir. CachedRowSet'de kaynak her zaman rdbms olmak zorunda degildir. Tablo
   * formatinda veri tutulan veriyi alabilir. Fakat bu islem icin CachedRowSet gibi baglantisiz
   * RowSet'e ait RowSetReader nesnesinin bu datayi okuyabilmesi icin implemente edilmedilir.
   */
  public void doExercisesOnProductWithCachedRowSet() {

    RowSetFactory rowSetFactory = null;
    try {
      rowSetFactory = RowSetProvider.newFactory();
    } catch (SQLException e) {

      throw new RuntimeException();
    }

    try (CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();
        Connection conn = DriverManager.getConnection(url, username, password)) {
      conn.setAutoCommit(false);
      cachedRowSet.setCommand("SELECT * FROM PRODUCTS WHERE CATEGORY_ID = 1");
      // Eger CachedRowSet kullanarak Db uzerinde degisiklikler yapmak istiyorsak primaryKey gibi
      // keyColumn parametresini setlemeliyiz.
      // Burada 1. kolonumuz id parametresini ifade ettigi icin 1 olarak veriyoruz
      cachedRowSet.setKeyColumns(new int[] {1});
      cachedRowSet.addRowSetListener(new ProductRowSetListener());

      /**
       * Execute metodu calistiginda yukarida verdigimiz parametrelerde datasource olusur.
       * Database'e connection acilir ve veriler okunur. Sonrasinda connection close edilir.
       * JdbcRowSet ile temel farki buradadir.
       */
      cachedRowSet.execute(conn);

      cachedRowSet.absolute(10);
      System.out.println(
          cachedRowSet.getInt("product_id") + "  :  " + cachedRowSet.getFloat("unit_price"));
      cachedRowSet.updateFloat("unit_price", 28);
      cachedRowSet.updateRow();
      cachedRowSet.acceptChanges(conn);

      //      cachedRowSet.moveToInsertRow();
      //      cachedRowSet.updateInt(1, 96);
      //      cachedRowSet.updateString(2, "YeniUrun");
      //      cachedRowSet.updateInt(3, 2);
      //      cachedRowSet.updateInt(4, 1);
      //      cachedRowSet.updateString(5, "20x30");
      //      cachedRowSet.updateFloat(6, 61.61f);
      //      cachedRowSet.updateInt(7, 61);
      //      cachedRowSet.updateInt(8, 5);
      //      cachedRowSet.updateInt(9, 15);
      //      cachedRowSet.updateInt(10, 1);
      //      cachedRowSet.insertRow();
      //      cachedRowSet.moveToCurrentRow();
      //      cachedRowSet.acceptChanges(conn);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * JoinRowSet nesneleri RowSet nesneleri uzerinde SQL JOIN yapilari kurmak icin kullanilir.
   * JoinRowSet'lerde suan sadece INNER JOIN tipi desteklenmektedir.
   */
  public void doExercisesOnProductWithJoinRowSet() {

    RowSetFactory rowSetFactory = null;
    try {
      rowSetFactory = RowSetProvider.newFactory();
    } catch (SQLException e) {

      throw new RuntimeException();
    }

    try (CachedRowSet products = rowSetFactory.createCachedRowSet();
        CachedRowSet orderDetails = rowSetFactory.createCachedRowSet();
        JoinRowSet joinRowSet = rowSetFactory.createJoinRowSet();
        Connection conn = DriverManager.getConnection(url, username, password)) {
      conn.setAutoCommit(false);
      products.setCommand("SELECT * FROM PRODUCTS");
      products.setKeyColumns(new int[] {1});
      products.execute(conn);

      orderDetails.setCommand("SELECT * FROM ORDER_DETAILS");
      orderDetails.setKeyColumns(new int[] {1});
      orderDetails.execute(conn);

      joinRowSet.setJoinType(JoinRowSet.INNER_JOIN);
      joinRowSet.addRowSet(products, "product_id");
      joinRowSet.addRowSet(orderDetails, "product_id");

      while (joinRowSet.next()) {
        int orderId = joinRowSet.getInt("order_id");
        int product_id = joinRowSet.getInt("product_id");
        String productName = joinRowSet.getString("product_name");

        System.out.println(
            "order id : "
                + orderId
                + " product id : "
                + product_id
                + "product name : "
                + productName);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * FilteredRowSet ile cektigimiz verileri belirledigimiz bir sarta gore filtreleyebiliriz.
   */
  public void doExercisesOnProductWithFilteredRowSet() {

    RowSetFactory rowSetFactory = null;
    try {
      rowSetFactory = RowSetProvider.newFactory();
    } catch (SQLException e) {

      throw new RuntimeException();
    }

    try (Connection conn = DriverManager.getConnection(url, username, password);
        FilteredRowSet filteredRowSet = rowSetFactory.createFilteredRowSet(); ) {
      conn.setAutoCommit(false);
      filteredRowSet.setCommand("SELECT * FROM PRODUCTS");
      filteredRowSet.execute(conn);
      filteredRowSet.setFilter(new CategoryFilter(1));

      while (filteredRowSet.next()) {
        System.out.println(filteredRowSet.getInt(1));
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * WebRowSet nesneleri CachedRowSet objelerine ek olarak xml okuyup yazabilirler.
   */
  public void doExercisesOnProductWithFWebRowSet() {

    RowSetFactory rowSetFactory = null;
    try {
      rowSetFactory = RowSetProvider.newFactory();
    } catch (SQLException e) {

      throw new RuntimeException();
    }

    try (Connection conn = DriverManager.getConnection(url, username, password);
        WebRowSet webRowSet = rowSetFactory.createWebRowSet(); ) {
      conn.setAutoCommit(false);
      webRowSet.setCommand("SELECT * FROM PRODUCTS WHERE CATEGORY_ID=8");
      webRowSet.setKeyColumns(new int[]{1});
      webRowSet.execute(conn);

      FileOutputStream fileOutputStream = new FileOutputStream("D:\\PersonalRepo\\jdbcTutorialWithExamples\\src\\main\\resources\\products.xml");

      webRowSet.writeXml(fileOutputStream);
      FileInputStream fileInputStream = new FileInputStream("D:\\PersonalRepo\\jdbcTutorialWithExamples\\src\\main\\resources\\products.xml");
      webRowSet.readXml(fileInputStream);
      while(webRowSet.next()){
        System.out.println(webRowSet.getInt(1));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


}
