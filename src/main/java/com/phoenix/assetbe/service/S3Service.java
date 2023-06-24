package com.phoenix.assetbe.service;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.phoenix.assetbe.core.exception.Exception500;
import com.phoenix.assetbe.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    // 업로드
    public UserResponse.uploadOutDTO upload(MultipartFile multipartFile, String dirName) {
        try {
            File uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("Multipart to File 전환 실패"));
            String uploadedUrl = upload(uploadFile, dirName);
            return new UserResponse.uploadOutDTO(uploadedUrl);
        } catch (Exception e) {
            throw new Exception500("파일 업로드 실패 : " + e.getMessage());
        }
    }

    private String upload(File uploadFile, String dirName) {
        try {
            String uniqueFileName = generateUniqueFileName(uploadFile.getName()); // 고유한 파일명 생성
            String fileName = dirName + "/" + uniqueFileName; // 디렉토리명과 결합하여 전체 파일 경로 생성
            String uploadImageUrl = putS3(uploadFile, fileName);

            removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

            return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
        } catch (Exception e) {
            throw new Exception500("파일 업로드 실패 : " + e.getMessage());
        }
    }


    private String putS3(File uploadFile, String fileName) {
        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, fileName, uploadFile)
                            .withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드 됨
            );
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (Exception e) {
            throw new Exception500("S3 업로드 실패 : " + e.getMessage());
        }
    }

    private void removeNewFile(File targetFile) {
        try {
            if (targetFile.delete()) {
                log.info("파일이 삭제되었습니다.");
            } else {
                log.info("파일이 삭제되지 못했습니다.");
            }
        } catch (Exception e) {
            throw new Exception500("파일 삭제 실패: " + e.getMessage());
        }
    }

    private Optional<File> convert(MultipartFile file) {
        try {
            File convertFile = new File(file.getOriginalFilename());
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(file.getBytes());
                }
                return Optional.of(convertFile);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new Exception500("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 삭제
    public void removeFile(String removeFile) {
        try {
            String fileName = getFileNameFromUrl(removeFile);
            deleteS3File(fileName);
        } catch (Exception e) {
            throw new Exception500("파일 삭제 실패: " + e.getMessage());
        }
    }

    private void deleteS3File(String fileName) {
        try {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
            log.info("S3 파일이 삭제되었습니다. 파일명: " + fileName);
        } catch (Exception e) {
            throw new Exception500("파일 삭제 실패: " + e.getMessage());
        }
    }

    private String getFileNameFromUrl(String fileUrl) {
        try {
            String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
            String[] urlParts = decodedUrl.split("/");
            return urlParts[urlParts.length - 1];
        } catch (Exception e) {
            throw new Exception500("URL 디코딩 실패: " + e.getMessage());
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        try {
            String fileExtension = FilenameUtils.getExtension(originalFileName); // 파일 확장자 추출
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension; // UUID와 확장자를 조합하여 고유한 파일명 생성
            return uniqueFileName;
        } catch (Exception e) {
            throw new Exception500("고유 파일명 생성 실패: " + e.getMessage());
        }
    }
}
