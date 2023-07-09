package dongsungTest;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.Statement;

public class UpdateTest {

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 try {
		  Class.forName("com.ibm.as400.access.AS400JDBCDriver");
	      // 데이터베이스 연결 설정
	      String url = "jdbc:as400://210.181.230.69;libraries=TESTSERVER";
	      String username = "DS4";
	      String password = "LJY";
	      Connection con = DriverManager.getConnection(url, username, password);
	      
	      // SQL 쿼리 실행
	      String query = "UPDATE SALDBF.SENMIV S  SET S.SDMSTP ='2', S.SDMUS2 ='TEST' WHERE S.SDMYMD ='20230428' AND S.SDMSTP ='1' ";
	      Statement stmt = con.createStatement();
	      int rowsUpdated = stmt.executeUpdate(query);
	      
	      // 결과 출력
	      System.out.println(rowsUpdated + " rows updated");
	      System.out.println("성공");
	      
	      // 연결 종료
	      con.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	      System.out.println("실패");
        }	 
			 
	
	}
}
//	public void update() {
//		 try {
//			  Class.forName("com.ibm.as400.access.AS400JDBCDriver");
//		      // 데이터베이스 연결 설정
//		      String url = "jdbc:as400://210.181.230.69;libraries=TESTSERVER";
//		      String username = "DS4";
//		      String password = "LJY";
//		      Connection con = DriverManager.getConnection(url, username, password);
//		      
//		      // SQL 쿼리 실행
//		      String query = "UPDATE SALDBF.SENMIV S  SET S.SDMSTP ='2', S.SDMUS2 ='TEST' WHERE S.SDMYMD ='20230428' AND S.SDMSTP ='1' ";
//		      Statement stmt = con.createStatement();
//		      int rowsUpdated = stmt.executeUpdate(query);
//		      
//		      // 결과 출력
//		      System.out.println(rowsUpdated + " rows updated");
//		      System.out.println("성공");
//		      
//		      // 연결 종료
//		      con.close();
//		    } catch (Exception e) {
//		      e.printStackTrace();
//		      System.out.println("실패");
//		    }	
// }

