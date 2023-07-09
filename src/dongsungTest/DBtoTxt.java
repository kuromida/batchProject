package dongsungTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JOptionPane;

//import com.mysql.cj.protocol.Resultset;

public class DBtoTxt {

	ResultSet rs = null;
	String currentDate=null;


	public static void main(String[] args) throws InterruptedException {

	
		DBtoTxt dTxt = new DBtoTxt();
		dTxt.delFile(); //폴더 내  파일삭제 
        dTxt.dbConnect(dTxt);//DB연결후 TXT 추출
        dTxt.ftpUploader();//FTP서버로 파일 전송 
      
		
	}

	
	// 폴더 내 파일삭제
	public void delFile() {
		String FilePath = "C:\\BPDATA";
		File FileList = new File(FilePath);

		// 해당 폴더의 전체 파일리스트 조회
		String fileList[] = FileList.list();

		// 전체파일
		for (int i = 0; i < fileList.length; i++) {
			// 파일명 조회
			String FileName = fileList[i];

			// 파일명 R015001로 시작하는 문자열 찾기
			if (FileName.contains("R015001")) {
				// 존재하면 파일 삭제

				File deleteFile = new File(FilePath + "\\" + FileName);
				deleteFile.delete();
				System.out.println("file delete success");
			}
		}
	}
	
	
	public void dbConnect (DBtoTxt dTxt) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
	
		try {

			Class.forName("com.ibm.as400.access.AS400JDBCDriver");
			// 테스트 서버정보
			String url = "jdbc:as400://210.181.***.**;libraries=TESTSERVER"; 
			conn = DriverManager.getConnection(url, "TEST", "TEST");
			
			
	

			stmt = conn.createStatement();

			//테스트 
			//String sql = "SELECT DKYYMM, DKJNNO, DKGCOD,DKGNAM,DKPOS,DKJUSO,DKTEL1,DKTEL2,DKDVSN,DKBOX,DKSNOTE FROM TXTDBF.DSKLOGIS";
			// 운영DB
            String sql = "SELECT DKYYMM, DKJNNO, DKGCOD,DKGNAM,DKPOS,DKJUSO,DKTEL1,DKTEL2,DKDVSN,DKBOX,DKSNOTE FROM SALDBF.DSKLOGIS";
            
			
             rs = stmt.executeQuery(sql);

			dTxt.fileWrite(rs);

			
			System.out.println("file download Success");
			
			// senmiv 업데이트하기 
			String date = currentDate.substring(0,8);
			String time = currentDate.substring(8,14);
		
			//테스트 
			//String qry = "UPDATE SALDBF.SENMIV SET SDMCK2 ='Y', SDMSTP ='2', SDMUS2 ='ADMIN', SDMDN2=' ', SDMIL2="+date+",SDMIM2 ="+time
			//		+",SDMFL2 ='R015001"+currentDate +".txt'"
			//		+ " WHERE  SDMYMD in (select substring(DKJNNO,1,8) from TXTDBF.DSKLOGIS) and  SDMJPN in (select substring(DKJNNO,9,7) from TXTDBF.DSKLOGIS)";
			
			//운영정보
			  String qry = "UPDATE SALDBF.SENMIV SET SDMCK2 ='Y', SDMSTP ='2', SDMUS2 ='ADMIN', SDMDN2=' ', SDMIL2="+date+",SDMIM2 ="+time
					+",SDMFL2 ='R015001"+currentDate +".txt'"
					+ " WHERE  SDMYMD in (select substring(DKJNNO,1,8) from SALDBF.DSKLOGIS) and  SDMJPN in (select substring(DKJNNO,9,7) from SALDBF.DSKLOGIS)";
			  
			 
			int rowsAffected = stmt.executeUpdate(qry);
			
			System.out.println(rowsAffected + " rows affected.");
			
		} catch (ClassNotFoundException e) {
			System.out.println("Driver loading fail");
			JOptionPane.showMessageDialog(null, "Driver loading fail");

		} catch (SQLException e) {
			System.out.println("error" + e);
			JOptionPane.showMessageDialog(null, "error" + e);
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// DB 파일 TXT로 쓰기 
	public void fileWrite(ResultSet rs) {
		this.rs = rs;
		Date date = new Date();
		Locale currentLocal = new Locale("KOREAN", "KOREA");
		String pattern = "yyyyMMddHHmmss";
		SimpleDateFormat fmt = new SimpleDateFormat(pattern, currentLocal);
		currentDate = fmt.format(date);
		// BufferedWriter out = null;

		try {
            String fileName = "c:\\BPDATA\\R015001" + currentDate + ".txt";
			File file = new File(fileName);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fw);

			

			while (rs.next()) {
				writer.write((rs.getString(1)).trim() + "\t");
				writer.write((rs.getString(2)).trim() + "\t");
				writer.write((rs.getString(3)).trim() + "\t");
				writer.write((rs.getString(4)).trim() + "\t");
				writer.write((rs.getString(5)).trim() + "\t");
				writer.write((rs.getString(6)).trim() + "\t");
				writer.write((rs.getString(7)).trim() + "\t");
				writer.write((rs.getString(8)).trim() + "\t");
				writer.write((rs.getString(9)).trim() + "\t");
				writer.write((rs.getString(10)).trim() + "\t");
				writer.write((rs.getString(11)).trim() + "\n");

			}

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	


	// FTP 로 파일 업로드 메소드
	public void ftpUploader() throws InterruptedException {
		String server = "ftp.klogis.com";
		int port = 21;
		String user = "cust015";
		String password = "ehdtjd";

		FTPUploader uploader = new FTPUploader(server, port, user, password);
		try {
			uploader.connect();
			String FilePath = "C:\\BPDATA";
			File localFile = new File(FilePath);
			String remoteFilePath = "\\cust015\\rcvdata\\";

			// 해당 폴더의 전체 파일리스트 조회
			String localList[] = localFile.list();
			// 전체파일
			for (int i = 0; i < localList.length; i++) {
				// 파일명 조회
				String FileName = localList[i];

				// 파일명 R015001로 시작하는 문자열 찾기
				if (FileName.contains("R015001")) {
					// 존재하면
					File uploadFile = new File(FilePath + "\\" + FileName);
					uploader.upload(uploadFile, remoteFilePath + FileName);
					uploader.disconnect();
					

				}
			}

			System.out.println("File uploaded successfully.");
			//JOptionPane.showMessageDialog(null, "파일업로드 성공");
			Thread.sleep(1000);
		} catch (IOException e) {
			System.out.println("Error uploading file: " + e.getMessage());
		}

	}
	

	

}
