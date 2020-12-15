package pl.edu.agh.pawicao.studying_squirrels_api.config.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.specialized.BlobOutputStream;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StorageClient {
  private static StorageClient storageClient;
  private BlobContainerClient containerClient;
  private StorageClient() {  }
  private static String connectionString = "DefaultEndpointsProtocol=https;" +
                                           "AccountName=ssbtstorage;" +
                                           "AccountKey=9xTKONsgNC7XLHf2PK3dvEfD+Fq8nwKIqTJ9meyFRA1vyqr91s/B/OqvnKsb3V+W/o3gg/0WCd1BRHNGWa/Y4w==;" +
                                           "EndpointSuffix=core.windows.net";

  public static StorageClient getInstance() {
    if (storageClient==null)
    {
      storageClient = new StorageClient();
      BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
      storageClient.containerClient = blobServiceClient.getBlobContainerClient("ssbt-service-container");
    }
    return storageClient;
  }

  public boolean uploadFile(MultipartFile file, String name) {
    try {
      BlobClient blobClient = containerClient.getBlobClient(name);
      BlockBlobClient blockBlobClient = blobClient.getBlockBlobClient();
      blockBlobClient.upload(file.getInputStream(), file.getSize(), true);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deleteFile(String name) {

    BlobClient blobClient = containerClient.getBlobClient(name);
    BlockBlobClient blockBlobClient = blobClient.getBlockBlobClient();
    blockBlobClient.delete();
    return true;
  }

  public ByteArrayOutputStream getFile(String name) {
    BlobClient blobClient = containerClient.getBlobClient(name);
    BlockBlobClient blockBlobClient = blobClient.getBlockBlobClient();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    blockBlobClient.download(outputStream);
    return outputStream;
  }
}
