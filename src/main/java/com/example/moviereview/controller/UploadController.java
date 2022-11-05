package com.example.moviereview.controller;

import com.example.moviereview.dto.UploadResultDTO;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

@RestController
public class UploadController {

    @Value("${com.example.upload.path}")            // application.properties 의 변수
    private String uploadPath;

    @PostMapping("/uploadAjax")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) {

        List<UploadResultDTO> resultDTOList = new ArrayList<>();
        for (MultipartFile uploadFile : uploadFiles) {
            // 이미지 파일만 업로드 가능
            if (uploadFile.getContentType().startsWith("image") == false) {
                // 이미지가 아닌 경우 403 Forbidden 반환
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // 실제 파일 이름 IE 나 Edge 는 전체 경로가 들어오므로
            String originalName = uploadFile.getOriginalFilename();

            String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

            // 날짜 폴더 생성
            String folderPath = makeFolder();

            // UUID
            String uuid = UUID.randomUUID().toString();

            // 저장할 파일 이름 중간에 "_"를 이용해 구분
            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

            Path savePath = Paths.get(saveName);

            try {
                uploadFile.transferTo(savePath);        // 실제 이미지 저장 (원본 파일)

                // 썸네일 생성 -> 썸네일 파일 이름은 중간에 s_ 로 시작
                String thubmnailSaveName = uploadPath + File.separator + folderPath + File.separator + "s_" + uuid + "_" + fileName;

                File thumbnailFile = new File(thubmnailSaveName);

                // 썸네일 생성
                Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);

                resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
    }

    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = str.replace("/", File.separator);

        // make folder
        File uploadPatheFolder = new File(uploadPath, folderPath);

        if (uploadPatheFolder.exists() == false) {
            uploadPatheFolder.mkdirs();
        }

        return folderPath;
    }

    @PostMapping("/removeFile")
    public ResponseEntity<Boolean> removeFile(String fileName) {
        String srcFileName = null;

        try {
            srcFileName = URLDecoder.decode(fileName, "UTF-8");
            File file = new File(uploadPath + File.separator + srcFileName);

            boolean result = file.delete();

            File thumbnail = new File(file.getParent(), "s_" + file.getName());

            result = thumbnail.delete();

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // File Upload Test
    // 오직 파일만을 업로드 하기 위한 방법. 그 외의 데이터를 가져올 수가 없다.
    @PostMapping("/upload")
    @ResponseBody
    public void FileUpload(MultipartFile uploadfile, Model model) {
        try {
            if (!uploadfile.isEmpty()) {
                System.out.println("file name : " + uploadfile.getOriginalFilename());
                System.out.println("file content type : " + uploadfile.getContentType());
                uploadfile.transferTo(new File(uploadfile.getOriginalFilename()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/upload2")
    @ResponseBody
    public void FileUpload2(MultipartHttpServletRequest request) {
        try {
            System.out.println("/upload2");

            // HttpServletRequest 로 넘어오는 모든 request 값 key, value 확인
            Enumeration params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String name = (String) params.nextElement();
                System.out.print(name + " : " + request.getParameter(name) + " . ");
            }
            System.out.println();

            String fileName = "Mollang_" + request.getParameter("file");
            System.out.println(fileName);

            MultipartFile file = request.getFile("file");
            System.out.println(file.getOriginalFilename());
            file.transferTo(new File(file.getOriginalFilename()));

        } catch (Exception e) {

        }
    }

    // String size 파라미터를 추가해 원본파일인지 썸네일인지 구분할 수 있도록 구성한다.
    // -> 만약 size 변수의 값이 1인 경우 원본 파일을 전송한다.
    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName, String size) {
        ResponseEntity<byte[]> result = null;

        try {
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");

            File file = new File(uploadPath + File.separator + srcFileName);

            if (size != null && size.equals("1")) {
                file = new File(file.getParent(), file.getName().substring(2));
            }

            HttpHeaders headers = new HttpHeaders();

            // MIME타입 처리
            headers.add("Content-type", Files.probeContentType(file.toPath()));

            // 파일 데이터 처리
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
