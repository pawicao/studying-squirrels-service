package pl.edu.agh.pawicao.studying_squirrels_api.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

  public static MultipartFile getNewFile(String fileName, MultipartFile currentFile){
    return new MultipartFile() {
      @Override
      public String getName() {
        return currentFile.getName();
      }

      @Override
      public String getOriginalFilename() {
        return fileName;
      }

      @Override
      public String getContentType() {
        return currentFile.getContentType();
      }

      @Override
      public boolean isEmpty() {
        return currentFile.isEmpty();
      }

      @Override
      public long getSize() {
        return currentFile.getSize();
      }

      @Override
      public byte[] getBytes() throws IOException {
        return currentFile.getBytes();
      }

      @Override
      public InputStream getInputStream() throws IOException {
        return currentFile.getInputStream();
      }

      @Override
      public void transferTo(File file) throws IOException, IllegalStateException {

      }
    };
  }
}
