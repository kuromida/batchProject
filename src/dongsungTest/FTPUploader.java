package dongsungTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUploader {
	private String server;
	private int port;
	private String user;
	private String password;
	private FTPClient ftpClient;

	public FTPUploader(String server, int port, String user, String password) {
		this.server = server;
		this.port = port;
		this.user = user;
		this.password = password;
	}
	public void connect() throws SocketException, IOException {
		ftpClient = new FTPClient();
		ftpClient.connect(server, port);
		int replyCode = ftpClient.getReplyCode();//응답이 정상인지 확인
		if (!FTPReply.isPositiveCompletion(replyCode)) {//비정상일 경우 : 연결을 끊고 종료
			throw new IOException("FTP server refused connection.");
		}
		ftpClient.login(user, password);//ftp 서버 로그인 
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}
	public void upload(File file, String remoteFilePath) throws IOException {
		try (InputStream inputStream = new FileInputStream(file)) { //업로드할 파일 읽기
			OutputStream outputStream = ftpClient.storeFileStream(remoteFilePath);
			if (outputStream == null) { 
				throw new IOException("Failed to upload file.");
			}
			byte[] buffer = new byte[4096];//버퍼 생성 
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {//파일 데이터 읽기  입력 스트림에서 -1반환될때까지 반복
				outputStream.write(buffer, 0, bytesRead);// 읽은 데이터 출력스트림에 쓰기 
			}
			outputStream.close();// 출력 스트림 닫기
			inputStream.close();//입력스트림 닫기
		}
		boolean success = ftpClient.completePendingCommand();//ftp서버에서 이전명령이 성공적으로 완료됬는지 확인 
		if (!success) {
			throw new IOException("Failed to upload file.");
		}
	}
	public void disconnect() throws IOException {
		ftpClient.disconnect();
	}

}
