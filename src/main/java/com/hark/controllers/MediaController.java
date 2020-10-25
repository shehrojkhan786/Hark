/**
 * 
 */
package com.hark.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hark.model.payload.response.MessageResponse;

/**
 * @author shkhan
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/media")
public class MediaController {

	private final String fileBasePath = "/files/media/";

	@PostMapping("/upload")
	public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file,
			@RequestParam("discussionId") String discussionId, @RequestParam("userId") String userId) throws Exception {

		if (file == null) {
			throw new RuntimeException("You must select the a file for uploading");
		}

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String filePath = String.join("/", fileBasePath, discussionId, userId, fileName);
		Path path = Paths.get(filePath);
		try {
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(filePath).toUriString();

		// Do processing with uploaded file data in Service layer
		return new ResponseEntity<String>(fileDownloadUri, HttpStatus.OK);
	}

	@GetMapping("/download/{filePath:.+}")
	public ResponseEntity downloadFileFromLocal(@PathVariable String filePath, HttpRequestHandlerServlet request) {
		Path path = Paths.get(fileBasePath + filePath);
		Resource resource = null;
		MediaType mediaType = null;
		String contentType = null;
		try {
			resource = new UrlResource(path.toUri());
			// Try to determine file's content type
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new MessageResponse("Exception occured: "+e.getLocalizedMessage()));
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new MessageResponse("Exception occured: "+e.getLocalizedMessage()));
		} finally {
			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
