package org.sdf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.net.ftp.FTPClient;
import org.sdf.log.Log;

public class FTPUtil {

	private String locale;

	private String fileName;
	private String localDir;
	private String remoteDir;
	private String encode;
	private boolean putResult;

	private int port;
	private String host;
	private String user;
	private String password;
	private boolean passive;
	FTPClient ftpClient = null;

	public FTPUtil() {
		super();
	}

	public void setPassive(boolean b) {
		this.passive = b;
	}

	public void setEncoding(String encode) {
		this.encode = encode;
	}

	public void setUserLocale(String locale) {
		this.locale = locale;
	}

	public void setPutResult(boolean putResult) {
		this.putResult = putResult;
	}

	public boolean getPutResult() {
		return this.putResult;
	}

	/**
	 * FTP 서버 접속
	 *
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean connect(String host, int port, String user, String password) {

		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;

		try {
			ftpClient = new FTPClient();
			if (this.encode == null || "".equals(this.encode)) {
				//ftpClient.setControlEncoding("EUC-KR");
				//ftpClient.setControlEncoding("UTF-8");
			} else {
				ftpClient.setControlEncoding(this.encode);

			}
			ftpClient.connect(host, port); // ftp서버 접속

			int reply = ftpClient.getReplyCode();

			Log.biz.info("Ftp[" + host + ":" + port + ":" + user + "/"
					+ password + "]  connected.");

			ftpClient.login(user, password);

			ftpClient.setBufferSize(200000);
			ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
			ftpClient.setFileTransferMode(ftpClient.STREAM_TRANSFER_MODE);
			if (passive)
				ftpClient.pasv();

		} catch (Exception e) {
			Log.biz.err("Ftp[" + host + ":" + port + ":" + user + "/"
					+ password + "] not connected.", e);
			// errMsg = ms.getMessage("DS7103", locale); //FTP서버 접속 실패하였습니다.
			return false;
		}
		return true;
	}

	/**
	 * 파일 전송
	 *
	 * @param p_hostPath
	 * @param p_fileFullName
	 * @return
	 */
	public boolean put(String p_hostPath, String p_fileFullName)
			throws Exception {

		boolean isSuccess = false;

		try {

			File sendFile = new File(p_fileFullName);
			int strPos = p_fileFullName.lastIndexOf("/");
			String fileName = p_fileFullName.substring(strPos + 1,
					p_fileFullName.length());

			InputStream inputStream = new FileInputStream(sendFile);

			// mkDir(p_hostPath);

			// System.err.println("fileName : " + fileName);
			// System.err.println("p_hostPath + fileName : " + p_hostPath +"/" +
			// fileName);

			isSuccess = ftpClient.storeFile(p_hostPath + "/" + fileName,
					inputStream);

			Log.biz.info("FTP:" + p_fileFullName + "-->" + p_hostPath + "/"
					+ fileName + ":" + isSuccess);
			setPutResult(isSuccess);

			// String replyString = ftpClient.getReplyString();
			// System.err.println("replyString : " + replyString);

		} catch (Exception e) {
			Log.biz.err("FTP:" + p_fileFullName + " send faield.", e);
			isSuccess = false;
			throw new Exception();
		}

		return isSuccess;
	}

	/**
	 * 파일 전송
	 *
	 * @param p_hostPath
	 * @param p_fileFullName
	 * @return
	 */
	public boolean get(String remote_path, String local_dir)
			throws Exception {

		boolean isSuccess = false;

		try {
			int strPos = remote_path.lastIndexOf("/");
			String fileName = remote_path.substring(strPos + 1);
			File file = new File(local_dir, fileName);
			OutputStream stream = new FileOutputStream(file);

			// mkDir(p_hostPath);

			// System.err.println("fileName : " + fileName);
			// System.err.println("p_hostPath + fileName : " + p_hostPath +"/" +
			// fileName);

			isSuccess = ftpClient.retrieveFile(remote_path ,stream);

			Log.biz.info("FTP:" + remote_path + "-->" + file.getAbsolutePath() + ":" + isSuccess);
			setPutResult(isSuccess);

			// String replyString = ftpClient.getReplyString();
			// System.err.println("replyString : " + replyString);

		} catch (Exception e) {
			Log.biz.err("FTP:" + remote_path + " retrieve faield.", e);
			isSuccess = false;
			throw new Exception();
		}

		return isSuccess;
	}

	/*
	 * public void send(String remoteDir, File files[]) { int i;
	 *
	 * try { if(files == null || files.length == 0) { return; } }
	 * catch(Exception e) { // throw new SystemException(e); }
	 *
	 * for(i = 0; i < files.length; i++) { String remotePath = remoteDir + "/" +
	 * files[i].getName(); put(remotePath, files[i].toString()); }
	 *
	 * }
	 */
	/**
	 * FTP 서버에 디렉토리 생성.
	 *
	 * @param p_dirName
	 */
	public void mkDir(String p_dirName) {
		if (p_dirName == null || p_dirName.equals("")) {
			return;
		}

		try {
			String dirs[] = p_dirName.split("/");
			int length = dirs.length;
			if (p_dirName.startsWith("/") && length - 1 >= 2) {
				String dir = "";
				for (int i = 0; i < dirs.length; i++) {
					dir = dir + dirs[i] + "/";
					ftpClient.makeDirectory(dir);
				}

				// System.err.println("making Directory 1 : " + p_dirName);
			} else {
				ftpClient.makeDirectory(p_dirName);
				// System.err.println("making Directory 2 : " + p_dirName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rename(String from, String to) {
		try {
			to.replace('\\', '/');
			String path = to.substring(1, to.lastIndexOf("/") + 1);
			if (!"".equals(path)) {
				mkDir(path);
			}
			if (!ftpClient.rename(from, to)) {
				// System.err.println("Cannot Rename File From " + from + " To "
				// + to);
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkEncoding(String fileName, String encoding) {
		try {
			byte[] bytes = fileName.getBytes(encoding);
			String decoded = new String(bytes, encoding);

			return fileName.equals(decoded);
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	public boolean close() {
		boolean isSuccess = false;
		try {
			if (ftpClient != null) {
				ftpClient.quit();
				isSuccess = true;
			}
		} catch (Exception e) {
			isSuccess = false;
		}
		return isSuccess;
	}

	/**
	 * FTP 모듈 테스트
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		FTPUtil ftp = new FTPUtil();

		String host = "192.168.0.3";
		int port = 21;
		String user = "test";
		String password = "aaa123";

		try {
			//
			if (!ftp.connect(host, port, user, password)) {
				System.out.println("Error.");
			}

			//ftp.setEncoding("EUC-KR");
			ftp.setEncoding("UTF8");
			// ftp.put("", "");

			//ftp.put("", "/egene/tmp/src.zip");
			ftp.get("/data/test.txt", "D:/temp");
			// ftp.put("/0100012/HRRequest/bsm",
			// "c://project-workspace/di-hr-web/web/upfiles/bsm/人事企??.txt");
			// ftp.rename("/0100012/HRRequest/bsm/823.jpg",
			// "/0100012/HRRequest/bsm/임시.jpg");
			ftp.close();
			//System.out.println("End.");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}