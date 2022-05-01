package site.metacoding.blogv3.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import site.metacoding.blogv3.handler.ex.CustomException;

public class UtilFileUpload {

    // 파일을 쓰고 난 뒤 그 경로를 리턴해주는 메서드
    public static String write(String uploadFolder, MultipartFile file) {
        UUID uuid = UUID.randomUUID();

        String originalFilename = file.getOriginalFilename(); // 충돌나니까 UUID 사용
        String uuidFilename = uuid + "_" + originalFilename; // 파일 이름 변환
        try {
            // 하드에 저장
            Path filePath = Paths.get(uploadFolder + uuidFilename); // I/O 작업
            Files.write(filePath, file.getBytes());
        } catch (Exception e) {
            throw new CustomException("파일 업로드 실패");
        }

        // 경로 리턴
        return uuidFilename;
    }
}
