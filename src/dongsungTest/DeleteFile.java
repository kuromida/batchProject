package dongsungTest;

import java.io.File;

public class DeleteFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DeleteFile delFile = new DeleteFile();
		
		delFile.delFile();
		

	}
	
	       public void delFile() {
	    		 String FilePath = "C:\\BPDATA";
	             File FileList = new File(FilePath);

	             //해당 폴더의 전체 파일리스트 조회
	             String fileList[] = FileList.list();
	               
	             
	             //전체파일 
	             for(int i=0;i<fileList.length;i++) {
	          	//파일명 조회
	          	   String FileName = fileList[i];
	          	   
	          	   
	          	  //파일명 R015001로 시작하는 문자열 찾기 
	          	   if(FileName.contains("R015001")) {
	          		   //존재하면 파일 삭제 
	          		   
	          		   File deleteFile = new File(FilePath+"\\"+FileName);
	          		 System.out.println(deleteFile);
	          		   deleteFile.delete();
	          	   }
	             }
	       }

}
