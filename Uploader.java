import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Uploader {
  public static void main(String[] args) throws ProtocolException, IOException {
    Path csv = Paths.get("./" + (args.length < 1 ? "PN_Case_Study_-_Attachment.csv" : args[0]));

    try (BufferedReader r = Files.newBufferedReader(csv)) {
      int boundary = args.length < 2 ? 1000 : Integer.parseInt(args[1]);
      int count = 0;
      int chunkCount = 1;
      String line;
      StringBuilder content = new StringBuilder();
      while ((line = r.readLine()) != null) {
        if (count == boundary) {
          // send post
          URL obj = new URL("http://localhost:8080/promotions/upload");
          HttpURLConnection con = (HttpURLConnection) obj.openConnection();
          con.setRequestMethod("PUT");
          con.setRequestProperty("Content-Type", "application/json");

          String jsonInputString = "{\"chunkNumber\": " + chunkCount + ", \"content\": \"" + content.toString() + "\"}";

          // String params = "chunkNumber=" + chunkCount + "&content=" +
          // content.toString();
          con.setDoOutput(true);

          try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
          }

          int responseCode = con.getResponseCode();
          try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
              response.append(responseLine.trim());
            }
            System.out.println(response.toString());
          }

          chunkCount++;
          count = 0;
          content = new StringBuilder();
        }

        content.append(line).append("\\n");
        count++;
      }

      URL obj = new URL("http://localhost:8080/promotions/finish-upload");
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");
      int responseCode = con.getResponseCode();
      System.out.println(responseCode);
      try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
        System.out.println(response.toString());
      }

    }
  }
}