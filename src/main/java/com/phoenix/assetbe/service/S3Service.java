package com.phoenix.assetbe.service;

import com.phoenix.assetbe.core.config.S3Properties;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3Service {

    private final S3Client s3Client;

    private final S3Properties s3Properties;

    // 업로드
    public UserResponse.uploadOutDTO upload(MultipartFile multipartFile, String fileType) {

        String uploadFilePath = fileType + "/" + getFolderName();
        long fileSize = multipartFile.getSize();
        String extension = FilenameUtils.getExtension(multipartFile.getName());

        String originalFileName = multipartFile.getOriginalFilename();
        String uploadFileName = getUuidFileName(originalFileName);
        String uploadFileUrl = "";

        String keyName = "";

        try (InputStream inputStream = multipartFile.getInputStream()) {

            keyName = uploadFilePath + "/" + uploadFileName; // ex) 구분/년/월/일/파일.확장자

            // S3에 폴더 및 파일 업로드
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(keyName)
                    .build(), RequestBody.fromInputStream(inputStream, multipartFile.getSize()));

            // S3에 업로드한 폴더 및 파일 URL
            uploadFileUrl = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(keyName)
                    .build()).toExternalForm();

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception500("파일 업로드 실패 " + e.getMessage());
        }

        return new UserResponse.uploadOutDTO(keyName, fileSize, extension);
    }

    /**
     * S3에 업로드된 파일 삭제
     */
    public void deleteFile(String keyName) {

        try {
            boolean isObjectExist = false;
            try {
                HeadObjectResponse response = s3Client.headObject(HeadObjectRequest.builder()
                        .bucket(s3Properties.getBucket())
                        .key(keyName)
                        .build());

                int statusCode = response.sdkHttpResponse().statusCode();
                isObjectExist = statusCode == 200;
            } catch (S3Exception e) {
                if (e.statusCode() != 404) {
                    throw new Exception500("파일 삭제 실패 : " + e.getMessage());
                }
            }

            if (isObjectExist) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(s3Properties.getBucket())
                        .key(keyName)
                        .build());
            }
        } catch (Exception e) {
            throw new Exception500("파일 삭제 실패 : " + e.getMessage());
        }
    }

    /**
     * UUID 파일명 반환
     */
    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    /**
     * 년/월/일 폴더명 반환
     */
    private String getFolderName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String str = sdf.format(date);
        return str.replace("-", "/");
    }

}
