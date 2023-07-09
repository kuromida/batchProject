package dongsungTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class qryTest {

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
		      String qry = "SELECT DKJNNO FROM TXTDBF.DSKLOGIS";
		      Statement stmt = con.createStatement();
		      ResultSet rs = stmt.executeQuery(qry);

		      // 결과 저장
		      int size = 0;
		      while (rs.next()) {
		        size++;
		        
		      }
		      String[][] result = new String[size][1];
		     
//		      rs.beforeFirst();
		      int index = 0;
		      ResultSet rs2 = stmt.executeQuery(qry);
		      while (rs2.next()) {
		        result[index][0] = rs2.getString(1);
		     
		    
		        index++;
		      }

		 
		      // 결과 출력
		      for (int i = 0; i < size; i++) {
		    	  System.out.println(result[i][0] + " ");
		      }

		     
		      
		      
		      // 연결 종료
		      con.close();
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
	}

}
