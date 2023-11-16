package com.exhibition.modules.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
/**
    private final FileService fileService;

    @Value("${file.download.directory}")
    private String fileDownloadDirectory;

    @GetMapping("/attached/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long fileId) throws MalformedURLException {
        DownloadFile file = fileService.findById(fileId);
        String storedFileName = file.getAttachedFile().getStoredFileName();
        String uploadFileName = file.getAttachedFile().getUploadFileName();
        UrlResource resource = new UrlResource("file:" + fileDownloadDirectory + storedFileName);
        log.info("uploadFileName = {}", uploadFileName);
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDispostion = "attachment; filename=\"" + encodedUploadFileName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispostion)
                .body(resource);
    }
**/
}
