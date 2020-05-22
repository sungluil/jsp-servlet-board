package org.sdf.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

import org.sdf.log.Log;

/**
 * 파일 관련 지원 Util
 * @author jwpark
 *
 */
public class FileSupportUtil {
	
	
	/**
	 * 파일 생성
	 * @param content 내용
	 * @param filePath 파일 경로
	 */
	public static void create(String content, String filePath) throws IOException {
		
		BufferedOutputStream bos = null;
		
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			bos = new BufferedOutputStream(fos);
			
			bos.write(content.getBytes());
			
			bos.close();
		} catch(IOException ioe) {
			
			if(bos != null) {
				try { bos.close(); }catch(IOException ioe2) {}
			}
			Log.bat.err("[FileSupportUtil] File Write Error!", ioe);
			throw ioe;
		}
		
		/**
		 * 파일 읽기 권한
		 */
		File f = new File(filePath);
		f.setReadable(true, false);
		f.setWritable(true, false);
		f.setExecutable(true, false);
	}
	
	/**
	 * 파일 생성
	 * @param content 내용
	 * @param filePath 파일 경로
	 * @param encoding euc-kr
	 */
	public static void create(String content, String filePath, String encoding) throws IOException {
		
		BufferedWriter writer = null;
		
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			writer = new BufferedWriter(osw);

			writer.write(content);
			
			writer.close();
		} catch(IOException ioe) {
			
			if(writer != null) {
				try { writer.close(); }catch(IOException ioe2) {}
			}
			Log.bat.err("[FileSupportUtil] File Write Error!", ioe);
			throw ioe;
		}
		
		/**
		 * 파일 읽기 권한
		 */
		File f = new File(filePath);
		f.setReadable(true, false);
		f.setWritable(true, false);
		f.setExecutable(true, false);
	}

	/**
	 * 파일 이동
	 * @param targetPath 파일 전체 경로
	 * @param toPath 대상 전체 경로
	 */
	public static boolean move(String targetPath, String toPath){
		
		if(StringUtil.invalid(targetPath) || StringUtil.invalid(toPath)) {
			Log.bat.err("[FileSupportUtil] 대상 경로가 없습니다.\n target:" +targetPath + " to:" + toPath);
			return false;
		}
		
		File f = new File(targetPath);
		File toFile = new File(toPath);
		
		if(!f.isFile()) {
			Log.bat.err("[FileSupportUtil] 대상 파일이 없습니다.\n target:" +targetPath + " to:" + toPath);
			return false;
		}
		if(toFile.isFile()) {
			Log.bat.err("[FileSupportUtil] 해당 경로에 이미 파일이 존재합니다.\n target:" +targetPath + " to:" + toPath);
			return false;
		}
		
		if(f.renameTo(toFile)) {
			return true;
		} else {
			Log.bat.err("[FileSupportUtil] 파일 이동에 실패하였습니다.\n target:" +targetPath + " to:" + toPath);
			return false;
		}
	}
	
	/**
	 * 파일 복사. 
	 * @param targetPath
	 * @param toPath
	 * @return
	 * @throws IOException
	 */
	public static boolean copy(String targetPath, String toPath) throws IOException {
		
		if(StringUtil.invalid(targetPath) || StringUtil.invalid(toPath)) {
			Log.bat.err("[FileSupportUtil] 대상 경로가 없습니다.\n target:" +targetPath + " to:" + toPath);
			return false;
		}
		
		File f = new File(targetPath);
		
		if(!f.isFile()) {
			Log.bat.err("[FileSupportUtil] 대상 파일이 없습니다.\n target:" +targetPath + " to:" + toPath);
			return false;
		}
		
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		  
		try {
			inputStream = new FileInputStream(f);
			outputStream = new FileOutputStream(toPath);
			fcin = inputStream.getChannel();
			fcout = outputStream.getChannel();
			   
			long size = fcin.size();
			fcin.transferTo(0, size, fcout);
			
			return true;
		} catch(IOException ioe) {
			Log.bat.err("[FileSupportUtil] 파일 복사 오류", ioe);
			return false;
		}finally {
			//자원 해제
			try{
				if(fcout != null) fcout.close();
			}catch(IOException ioe){}
			try{
				if(fcin != null) fcin.close();
			}catch(IOException ioe){}
			try{
				if(outputStream != null) outputStream.close();
			}catch(IOException ioe){}
			try{
				if(inputStream != null) inputStream.close();
			}catch(IOException ioe){}
		}
	}
	
	/**
	 * 파일 삭제
	 * @param path 삭제 대상 경로
	 * @return 성공 여부
	 */
	public static boolean delete(String path) {
		if(StringUtil.invalid(path)) {
			Log.bat.err("[FileSupportUtil] delete 파일 경로가 없습니다.");
			return false;
		}
		
		File f = new File(path);
		
		if(!f.isFile()) {
			Log.bat.err("[FileSupportUtil] delete 파일이 없습니다. path=" + path);
			return false;
		}
		
		f.delete();
		
		return true;
	}
	
	public static void main(String args[]) {
		//buf.toString(), newfilePath
		
		String content = "HQHAI120Q59916     01KVH9960404119180000000000000000000000+000000000001.00000000000month       S0142194  2016022920160226㈜포스코ict / POSCO ICT., CO.,LTD.,KOREA                                        광양           IT서비스관리 시스템                                                                                                                                                                                                                             \n";
		String filePath = "D:\\tmp\\ITSM\\";
		String encoding = "euc-kr";
		try {
			FileSupportUtil.create(content, filePath +"TEST_euckr.txt", encoding);
			FileSupportUtil.create(content, filePath +"TEST_utf8.txt", "utf-8");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
