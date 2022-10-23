package com.example.moviereview.controller;

import com.example.moviereview.dto.UploadResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

@RestController
public class  UploadController {

    @Value("${com.example.upload.path}")            // application.properties 의 변수
    private String uploadPath;

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
            while(params.hasMoreElements()) {
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
                uploadFile.transferTo(savePath);        // 실제 이미지 저장
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
}
