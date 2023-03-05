package com.shop.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class FileService {
	
	public String uploadFile(String uploadPath,String originalFileName,
			byte[] fileData) throws Exception {
		UUID uuid = UUID.randomUUID();
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		String savedFileName = uuid.toString() + extension;
		String fileUploadFullUrl = uploadPath + File.separator + savedFileName;
		FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
		fos.write(fileData); //byte[]를 파일 출력 스트림에 입력합니다.
		//MultipartFile의 getBytes()해줄것임
		fos.close();
		return savedFileName; //업로드된 파일의 이름(uuid + .jpg확장자)
	}
	
	  public void deleteFile(String filePath) throws Exception{
	        File deleteFile = new File(filePath); //파일의 저장된 경로로 파일 객체 생성
	        if(deleteFile.exists()) {
	            deleteFile.delete();
	            log.info("파일을 삭제하였습니다.");
	        } else {
	            log.info("파일이 존재하지 않습니다.");
	        }
	   }
}
