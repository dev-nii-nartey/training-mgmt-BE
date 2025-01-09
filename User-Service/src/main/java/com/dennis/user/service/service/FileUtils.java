package com.dennis.user.service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

public class FileUtils {

    public static String convertMultipartFileToBase64(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes(); // Convert file to byte array
        return Base64.getEncoder().encodeToString(fileBytes); // Encode to Base64 string
    }
}