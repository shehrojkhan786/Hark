/**
 * 
 */
package com.hark.controllers;

import com.hark.model.enums.ResponseStatus;
import com.hark.model.payload.response.MessageResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;

/**
 * @author shkhan
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/media")
public class MediaController {

	private final String fileBasePath = "D:/files/media/";

	@PostMapping(value ="/upload/discussions", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<MessageResponse> uploadData(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
		MessageResponse response = new MessageResponse();
		String discussionRoomId = request.getHeader("discussionroomid");
		String userId = request.getHeader("userid");
		if (file == null) {
			response.setMessage("You must select the a file for uploading");
			response.setStatus(ResponseStatus.FAILED.name());
		}else if(StringUtils.isEmpty(userId)){
			response.setMessage("Kindly, tell me who is uploading the file. UserId is not available in headers.");
			response.setStatus(ResponseStatus.FAILED.name());
		}else if(StringUtils.isEmpty(discussionRoomId)){
			response.setMessage("Kindly, tell me for which discussion you are uploading the file. DiscussionRoomId is not available in headers.");
			response.setStatus(ResponseStatus.FAILED.name());
		}else {
			uploadFile(file, response, "discussions", userId, discussionRoomId);
		}
		// Do processing with uploaded file data in Service layer
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/upload/user", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<MessageResponse> uploadProfileImage(@RequestParam("file") MultipartFile file, HttpServletRequest request){
		MessageResponse response = new MessageResponse();
		String userId = request.getHeader("userid");
		//Optional<String> userId = headers.entrySet().stream().filter(key->"userid".equals(key)).map(Map.Entry::getKey).findFirst();
		if (file == null) {
			response.setMessage("You must select the a file for uploading");
			response.setStatus(ResponseStatus.FAILED.name());
		}else if(StringUtils.isEmpty(userId) && "null" == userId){
			response.setMessage("Kindly, tell me who is uploading the file. userId is not available in headers.");
			response.setStatus(ResponseStatus.FAILED.name());
		}else {
			System.out.println("Current userId is: "+userId);
			uploadFile(file, response, "users",userId,null);
		}
		// Do processing with uploaded file data in Service layer
		return ResponseEntity.ok(response);
	}

	private void uploadFile(MultipartFile file, MessageResponse response, String uploadFor,String userId, String discussionRoomId) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String userPath = userId;
		if(!StringUtils.isEmpty(discussionRoomId)){
			userPath = String.join("/",discussionRoomId,userId);
		}

		String filePath = String.join("/", fileBasePath, uploadFor, userPath, fileName);
		System.out.println(filePath+" Filepath : userId: "+userId);
		Path path = Paths.get(filePath);
		boolean filUploadSuccess = false;
		try {
			File directory = new File(path.toUri());
			if (! directory.exists()){
				directory.mkdirs();
			}
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			filUploadSuccess = true;
		} catch (IOException e) {
			System.out.println("Error while copying file " + e.getMessage());
			response.setMessage("Error while copying file. Kindly, try again!!!");
			response.setStatus(ResponseStatus.ERROR.name());
		}
		if (filUploadSuccess) {
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(filePath).toUriString();
			response.setMessage("File uploaded successfully.");
			response.setStatus(ResponseStatus.SUCCESS.name());
			response.setData(fileDownloadUri.split(fileBasePath)[1]);
		}
	}

	@GetMapping(value = "/download/",produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE,MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> downloadFileFromLocal(@RequestParam("file") String filePath, HttpRequestHandlerServlet request) {
		MessageResponse response = new MessageResponse();
		Path path = Paths.get(fileBasePath + filePath);
		Resource resource = null;
		MediaType mediaType = null;
		String contentType = null;
		boolean isSuccess = false;
		try {
			resource = new UrlResource(path.toUri());
			// Try to determine file's content type
			//contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			isSuccess = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			response.setMessage("Error: URL is incorrect-> "+e.getMessage());
			response.setStatus(ResponseStatus.ERROR.name());
		} catch (IOException e) {
			e.printStackTrace();
			response.setMessage("Error: While Fetching file-> "+e.getMessage()+" \nTry again.");
			response.setStatus(ResponseStatus.ERROR.name());
		}catch(Exception e){
			e.printStackTrace();
			response.setMessage("Error: While Fetching file-> "+e.getMessage()+" \nTry again.");
			response.setStatus(ResponseStatus.ERROR.name());
		}

		if(isSuccess) {
			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}
//			response.setMessage("File Found and retrieved.");
//			response.setStatus(ResponseStatus.SUCCESS.name());
//			response.setData(resource);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);

		}
		return  ResponseEntity.ok(response);
	}
}