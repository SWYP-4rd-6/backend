package com.swygbro.trip.backend.domain.s3.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.swygbro.trip.backend.domain.s3.exception.FailImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // S3에 이미지 업로드
    public String uploadImage(MultipartFile image) {
        String imageName = createImageName(image.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        try (InputStream inputStream = image.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, imageName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new FailImageUploadException();
        }

        return amazonS3.getUrl(bucketName, imageName).toString();
    }

    // S3에서 이미지 삭제
    public void deleteImage(String url) {
        String splitStr = ".com/";
        String fileName = url.substring(url.lastIndexOf(splitStr) + splitStr.length());

        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    // 이미지 업로드 전, 이미지 이름 난수화를 위해 UUID를 이용해 난수를 돌린다.
    public String createImageName(String imageName) {
        return "guide/" + UUID.randomUUID().toString().concat(getFileExtension(imageName));
    }

    // 이미지 확장자 추출
    public String getFileExtension(String imageName) {
        return imageName.substring(imageName.lastIndexOf("."));
    }
}
