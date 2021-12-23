package kr.omen.pico.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import kr.omen.pico.common.S3Uploader;
import kr.omen.pico.dao.CategoryRepository;
import kr.omen.pico.dao.PhotoRepository;
import kr.omen.pico.dao.PhotographerRepository;
import kr.omen.pico.dao.UserRepository;
import kr.omen.pico.dao.WorkRepository;
import kr.omen.pico.domain.Category;
import kr.omen.pico.domain.Photo;
import kr.omen.pico.domain.Photographer;
import kr.omen.pico.domain.User;
import kr.omen.pico.domain.Work;
import kr.omen.pico.domain.dto.WorkDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;

    private final PhotographerRepository photographerRepository;

    private final CategoryRepository categoryRepository;

    private final PhotoRepository photoRepository;

    private final S3Uploader s3Uploader;

    private final UserRepository userRepository;

    public Map<String, Object> uploadWork(WorkDTO.Create data) throws IOException{
        Map<String,Object> result = new HashMap<>();
        List<String> fileBase64 = data.getImages();
        List<String> type = new ArrayList<>();
        List<String> base64 = new ArrayList<>();
        for(String file : fileBase64){
            String[] base = file.split(",");
            int a = base[0].indexOf("/")+1;
            int b = base[0].indexOf(";");
            type.add(base[0].substring(a,b));
            base64.add(base[1]);
        }

        // 파일이 업로드되지 않았거나 사이즈가 큰 경우를 체크합니다.
        // 사이즈는 일반 바이트에서 1.33을 곱하면 BASE64 사이즈가 대략 나옵니다.
        // 여기다가 기본썸네일 지정해주기.
        if(fileBase64 == null || fileBase64.equals("")) {
            result.put("isFileInserted", false);
            result.put("uploadStatus", "FileIsNull");
            return result;
        }

        // 파일 업로드시 최대 길이(크기) 제한이지만 이게 있으면 작동안되는 경우가 많아서 주석처리
//        } else if(fileBase64.get(0).length() > 400000) {
//            result.put("isFileInserted", false);
//            result.put("uploadStatus", "FileIsTooBig");
//            return result;
//        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime current = ZonedDateTime.now();
        // 저장할 파일 경로를 지정합니다.

        String path = "src/main/resources/static/images/" + current.format(format);

        File file = new File(path);
        if(!file.exists()) {
            file.mkdir();
        }

        Photographer photographer = photographerRepository.findById(data.getPhotographerIdx()).get();
        Category category = categoryRepository.findById(data.getCategoryIdx()).get();

        Work work = workRepository.save(Work.builder()
                .content(data.getContent())
                .title(data.getTitle())
                .category(category)
                .photographer(photographer)
                .build());

        FileOutputStream fileOutputStream = null;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            for(int i=0;i<base64.size();i++) {

                byte[] decodedBytes = decoder.decode(base64.get(i));
                String fileName = Long.toString(System.nanoTime()) + "."+ type.get(i); // 파일네임은 서버에서 결정하거나 JSON에서 받아옵니다.
                file = new File(path + "/" + fileName);
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(decodedBytes);
                Long bytes = file.length();
                MultipartFile multipartFile = transfer(file);
                String url = s3Uploader.upload(multipartFile,"static");
                photoRepository.save(
                        Photo.builder()
                                .fileSize(Long.toString(bytes))
                                .storedFilePath(url)
                                .title(work.getTitle())
                                .work(work)
                                .build());
                if(i==0){
                    work.updateThumbnail(url);
                    workRepository.save(work);
                }

            }
            fileOutputStream.close();
            System.gc();
            System.runFinalization();
            File deleteFolder = new File(path);

            if(deleteFolder.exists()){
                File[] deleteFolderList = deleteFolder.listFiles();

                for (int j = 0; j < deleteFolderList.length; j++) {
                    deleteFolderList[j].delete();
                }

                if(deleteFolderList.length == 0 && deleteFolder.isDirectory()){
                    deleteFolder.delete();
                }
            }
            result.put("isFileInserted", true);
            result.put("uploadStatus", "AllSuccess");
        } catch(IOException e) {
            System.err.println(e);
            result.put("uploadStatus", "FileIsNotUploaded");
            result.put("isTTSInserted", false);
        }

        return result;
    }

    //File 객체를 multipartFile 객체로 변환시켜주는 메서드.(우리는 base64 방식이기 때문에 S3 사용을 위해 필요함.)
    public MultipartFile transfer(File file) throws IOException{
        FileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length() , file.getParentFile());
        InputStream input = new FileInputStream(file);
        OutputStream os = fileItem.getOutputStream();
        IOUtils.copy(input, os);
        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

        return multipartFile;
    }

    public WorkDTO.GetWork getWorkDetail(Long workIdx){
        Work work = workRepository.findById(workIdx).get();
        List<Photo> list = photoRepository.findByWork(work);
        if (work.equals(null)){
            return null;
        }
        List<String> photos = new ArrayList<>();
        for(Photo photo : list){
            photos.add(photo.getStoredFilePath());
        }
        WorkDTO.GetWork dto = new WorkDTO.GetWork(work,photos);
        return dto;
    }

    public List<WorkDTO.WorkCard> getWorkList(Long userIdx){
        User user = userRepository.findById(userIdx).get();
        Photographer photographer = photographerRepository.findByUser(user);
        List<Work> list = workRepository.findByPhotographer(photographer);
        List<WorkDTO.WorkCard> cards = new ArrayList<>();
        for(Work work : list){
            cards.add(new WorkDTO.WorkCard(work));
        }
        return cards;
    }
}
