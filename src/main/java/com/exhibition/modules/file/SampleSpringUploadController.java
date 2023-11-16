/**package com.exhibition.modules.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
public class SampleSpringUploadController {

    @Value("${file.download.directory}")
    private String fileDownloadDirectory;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam String sampleStringData , @RequestParam MultipartFile file , HttpServletRequest request) throws IOException {
        log.info("request = {}", request);
        log.info("sampleStringData = {}", sampleStringData);
        log.info("multipartFile = {}", file);
        if (!file.isEmpty()) {
            String downloadPath = fileDownloadDirectory + file.getOriginalFilename();
            log.info("파일 저장 경로 = {}", downloadPath);
            file.transferTo(new File(downloadPath));
        }
        return "upload-form";
    }


}**/
