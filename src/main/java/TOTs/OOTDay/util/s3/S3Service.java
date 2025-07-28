package TOTs.OOTDay.util.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface S3Service  {
    /**
     * S3에 파일을 업로드하고, 업로드된 파일의 CloudFront URL을 반환합니다.
     *
     * @param file   업로드할 파일 (MultipartFile)
     * @param domainType 도메인 타입
     * @param folderUuid 폴더 식별용 UUID
     * @return 업로드된 파일의 CloudFront URL (예: https://cdn.example.com/profiles/{UUID}/abc123.png)
     * @throws IOException 파일 읽기/업로드 중 오류 발생 시
     */
    String uploadFile(MultipartFile file, S3DomainType domainType, UUID folderUuid) throws IOException;

    /**
     * 지정한 S3 Key의 객체 존재 여부를 확인합니다.
     *
     * @param key S3 객체의 Key
     * @return 객체가 존재하면 true, 없으면 false
     */
    boolean doesObjectExist(String key);

    /**
     * S3에서 파일을 삭제합니다.
     *
     * @param key 삭제할 객체의 Key
     */
    void deleteFile(String key);
}
