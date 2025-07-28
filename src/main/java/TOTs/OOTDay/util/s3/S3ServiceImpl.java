package TOTs.OOTDay.util.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service{
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.url}")
    private String cloudFrontUrl;

    /**
     * S3에 파일 업로드 (파일명은 SHA-256 해시 기반으로 생성)
     *
     * @param file 업로드할 파일
     * @param domainType 도메인 타입
     * @param folderUuid 폴더 식별용 UUID
     * @return 업로드된 파일의 CloudFront URL
     * @throws IOException
     */

    @Override
    public String uploadFile(MultipartFile file, S3DomainType domainType, UUID folderUuid) throws IOException {
        String fileUrl = S3KeyGenerator.generateFileKeyWithHash(file, domainType, folderUuid);

        log.info("[S3 업로드 요청] key: {}", fileUrl);


        // 이미 존재하는지 확인
        if (!doesObjectExist(fileUrl)) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileUrl)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("[S3 업로드 완료] key: {}", fileUrl);
        } else {
            log.info("[S3에 동일한 파일 존재. 업로드 생략] key: {}", fileUrl);
        }

        return cloudFrontUrl + "/" + fileUrl;
    }

    @Override
    public boolean doesObjectExist(String key) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.headObject(headRequest);
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    @Override
    public void deleteFile(String key) {
        log.info("[S3 삭제 요청] key: {}", key);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try {
            s3Client.deleteObject(deleteRequest);
            log.info("[S3 삭제 완료] key: {}", key);
        } catch (S3Exception e) {
            log.error("[S3 삭제 실패] key: {}, message: {}", key, e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
}
