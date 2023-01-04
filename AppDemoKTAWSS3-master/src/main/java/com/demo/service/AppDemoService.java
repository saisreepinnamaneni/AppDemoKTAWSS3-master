package com.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class AppDemoService {
	
	public static final String BUCKET_NAME ="firstasws3demo1";
	
	@Autowired
	AmazonS3Client client;
	
	public String uploadFile(MultipartFile file) {
		
		String fileKey = System.currentTimeMillis()+"_"+file.getOriginalFilename();
		
		File fileObject = convertFile(file);
		
		client.putObject(BUCKET_NAME, fileKey, fileObject);
		fileObject.delete();
		
		return "File Uploaded on AWS server. File name: "+fileKey;
		
		
	}
	
	private File convertFile(MultipartFile file) {
		File fileOb = new File(file.getOriginalFilename());
		
		try(FileOutputStream outStream = new FileOutputStream(fileOb)){
			outStream.write(file.getBytes());
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		
		return fileOb;
		
	}
	
	
	public byte[] downloadFile(String fileKey) {
		S3Object s3Object = client.getObject(BUCKET_NAME, fileKey);
		S3ObjectInputStream inStream = s3Object.getObjectContent();
		
		try {
			byte[] content = IOUtils.toByteArray(inStream);
			return content;
			
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String deleteFile(String fileKey) {
		client.deleteObject(BUCKET_NAME, fileKey);
		return "File deleted from AWS server. File name: "+ fileKey;
	}
	

}
