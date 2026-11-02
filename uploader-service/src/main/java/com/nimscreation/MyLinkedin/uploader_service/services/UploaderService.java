package com.nimscreation.MyLinkedin.uploader_service.services;

import org.springframework.web.multipart.MultipartFile;

public interface UploaderService {

    String upload(MultipartFile file);

}
