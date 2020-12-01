package pl.edu.agh.pawicao.studying_squirrels_api.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class FileResponse {
  private String name;
  private String uri;
  private String type;
  private long size;

  @Override
  public String toString() {
    return "FileResponse{" +
           "name='" + name + '\'' +
           ", uri='" + uri + '\'' +
           ", type='" + type + '\'' +
           ", size=" + size +
           '}';
  }
}
