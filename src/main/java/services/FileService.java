package services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileService {


    void saveToJsonFile(Object object, String fileName);

    String readFromFile(String fileName);


    void uploadFile(MultipartFile file, String filename) throws IOException;
}