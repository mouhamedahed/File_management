package tn.esprit.spring.Service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.Part;

import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import Config.FileStorageProperties;
import tn.esprit.pi.exception.FileStorageException;
import tn.esprit.pi.exception.MyFileNotFoundException;
import tn.esprit.spring.URLUtils.AppConstants;

@Service
public class FileStorageServiceImpl {
	public final Path fileStorageLocation;

	@Autowired
	public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException(AppConstants.FILE_STORAGE_EXCEPTION_PATH_NOT_FOUND, ex);
		}
	}

	public String storeFile(MultipartFile file) throws IOException {
		File f = new File(AppConstants.TEMP_DIR + file.getOriginalFilename());

		f.createNewFile();
		FileOutputStream fout = new FileOutputStream(f);
		fout.write(file.getBytes());
		fout.close();

		if (f.exists())
			f.delete();

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (fileName.contains(AppConstants.INVALID_FILE_DELIMITER)) {

			}
			String newFileName = System.currentTimeMillis() + AppConstants.FILE_SEPERATOR + fileName;
			Path targetLocation = this.fileStorageLocation.resolve(newFileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return newFileName;
		} catch (IOException ex) {
			return "catsh";
		}

	}
	
	
	public String UploadImage(Part part){
		
		try (InputStream input = part.getInputStream()) {
			String fileName = part.getSubmittedFileName();
			String newFileName = System.currentTimeMillis() + AppConstants.FILE_SEPERATOR + fileName;
			Path targetLocation = this.fileStorageLocation.resolve(newFileName);    
			Files.copy(input, targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return newFileName;
		}
	    catch (IOException e) {
	        e.printStackTrace();
	        return "catsh";
	    }
	}
	
	public String UploadImages(UploadedFile img){
		
		try (InputStream input = img.getInputStream()) {
			String fileName = img.getFileName();
			String newFileName = System.currentTimeMillis() + AppConstants.FILE_SEPERATOR + fileName;
			Path targetLocation = this.fileStorageLocation.resolve(newFileName);    
			Files.copy(input, targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return newFileName;
		}
	    catch (IOException e) {
	        e.printStackTrace();
	        return "catsh";
	    }
	}
	

	
	

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException(AppConstants.FILE_NOT_FOUND + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException(AppConstants.FILE_NOT_FOUND + fileName, ex);
		}
	}

}
