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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;

//import com.mysql.cj.protocol.Resultset;

public class DBtoTxt {
   private static final Logger logger = Logger.getLogger(FTPBatchJob.class.getName());
   ResultSet rs = null;
   String currentDate = null;
   String FilePath = "C:\\BPDATA";
   String fileName;

   public static void main(String[] args) throws InterruptedException {

      DBtoTxt dTxt = new DBtoTxt();
      dTxt.delFile(); // 폴더 내 파일삭제

      dTxt.dbConnect(dTxt);// DB연결후 TXT 추출

      dTxt.ftpBatchLog(dTxt.FilePath); // ftp 파일 전송 및 배치로그

   }

   // 폴더 내 파일삭제
   public void delFile() {

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

   public void dbConnect(DBtoTxt dTxt) {
      ResultSet rs = null;
      Connection conn = null;
      Statement stmt = null;

      try {

         Class.forName("com.ibm.as400.access.AS400JDBCDriver");
         // 테스트 서버정보
//         String url = "jdbc:as400://210.***.***.**;libraries=TEST*****";
//         conn = DriverManager.getConnection(url, "TEST", "TEST");

          String url = "jdbc:as400://210.***.***.***;libraries=*****;"; // 운영 서버정보
          conn = DriverManager.getConnection(url, "REAL", "REAL"); // 운영서버 계정정보

         stmt = conn.createStatement();

         // 테스트
//         String sql = "SELECT DKYYMM, DKJNNO, DKGCOD,DKGNAM,DKPOS,DKJUSO,DKTEL1,DKTEL2,DKDVSN,DKBOX,DKSNOTE FROM TXTDBF.DSKLOGIS";
         // 운영DB
         String sql = "SELECT DKYYMM, DKJNNO,DKGCOD,DKGNAM,DKPOS,DKJUSO,DKTEL1,DKTEL2,DKDVSN,DKBOX,DKSNOTE FROM SALDBF.DSKLOGIS";

         rs = stmt.executeQuery(sql);

         dTxt.fileWrite(rs);

         System.out.println("file download Success");

         // senmiv 업데이트하기
         String date = currentDate.substring(0, 8);
         String time = currentDate.substring(8, 14);

         // 테스트
//         String qry = "UPDATE SALDBF.SENMIV SET SDMCK2 ='Y', SDMSTP ='2', SDMUS2 ='ADMIN', SDMDN2=' ', SDMIL2="
//               + date + ",SDMIM2 =" + time + ",SDMFL2 ='R015001" + currentDate + ".txt'"
//               + " WHERE  SDMYMD in (select substring(DKJNNO,1,8) from TXTDBF.DSKLOGIS) and  SDMJPN in (select substring(DKJNNO,9,7) from TXTDBF.DSKLOGIS)";

         // 운영정보
          String qry = "UPDATE SALDBF.SENMIV SET SDMCK2 ='Y', SDMSTP ='2', SDMUS2 ='ADMIN', SDMDN2=' ', SDMIL2="+date+",SDMIM2 ="+time
          +",SDMFL2 ='R015001"+currentDate +".txt'" + " WHERE SDMYMD in (select substring(DKJNNO,1,8) from SALDBF.DSKLOGIS) and SDMJPN in (select substring(DKJNNO,9,7) from SALDBF.DSKLOGIS)";

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
         String fileNam = "R015001" + currentDate + ".txt";
         this.fileName = fileNam;
         String filePathName = this.FilePath + "\\" + fileName;
         File file = new File(filePathName);

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

   public void ftpBatchLog(String FilePath) throws InterruptedException {
      String crrDate = currentDate.substring(0,8);
      // 로그 파일 경로
      String logFilePath = FilePath + "\\log\\" + crrDate + "_ftp.log";


      // 폴더 내 파찾기 

      File logFiles = new File(FilePath+"\\log");

      // 해당 폴더의 전체 파일리스트 조회
      String fileLists[] = logFiles.list();

      if (fileLists != null) {
         // 전체파일
         for (int i = 0; i < fileLists.length; i++) {
            // 파일명 조회
            String FileName = fileLists[i];

            // 파일명 ftp 포함된 문자열 찾기
            if (FileName.contains("ftp")) {
               
               String fileDateStr = FileName.substring(0, 8);
               
                int month1 =Integer.parseInt(fileDateStr.substring(4, 6));
                int month2 =Integer.parseInt(crrDate.substring(4,6));
                
                
               
               // 존재하면 파일이 현재 달과 차이가 있는지 확인
               if (month1!=month2) {
                  //System.out.println("Deleted file: " + FileName);
                  File files = new File(FilePath+"\\log\\"+FileName);
                  files.delete();
               } else {
               
                  //System.out.println("Failed to delete file: " + FileName);
               }

            }
         }
      }

      // FileHandler를 사용하여 로그 파일 핸들러 생성
      try {
         FileHandler fileHandler = new FileHandler(logFilePath, true);
         fileHandler.setFormatter(new SimpleFormatter());
         // Logger에 파일 핸들러 추가
         logger.addHandler(fileHandler);

      } catch (IOException e) {
         logger.log(Level.SEVERE, "로그 파일 핸들러를 생성하는 중에 오류가 발생했습니다.", e);
      }

      // FTP 전송 로직
      boolean ftpResult = ftpUploader();

      // FTP 전송 결과에 따라 로그 파일에 로그 작성
      if (ftpResult) {
         logger.info(this.fileName + "파일 전송에 성공했습니다.");
      } else {
         logger.warning(this.fileName + "파일 전송에 실패했습니다.");
      }

   }

   // FTP 로 파일 업로드 메소드
   public boolean ftpUploader() throws InterruptedException {
      String server = "ftp.*****.com";
      int port = 21;
      String user = "******";
      String password = "******";
   

      FTPUploader uploader = new FTPUploader(server, port, user, password);
      try {
         uploader.connect();
         String FilePath = this.FilePath;
         File localFile = new File(FilePath);
         String remoteFilePath = "\\******\\*****\\";

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
         // JOptionPane.showMessageDialog(null, "파일업로드 성공");
         Thread.sleep(1000);
         return true;
      } catch (IOException e) {
         System.out.println("Error uploading file: " + e.getMessage());
         return false;
      }

   }

}