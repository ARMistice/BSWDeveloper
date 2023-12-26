package de.brettspielwelt.develop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Uploader {
    public static void upload() {
        String fileToUpload = "publish.zip"; 
        String targetUrl = "http://localhost:8000/upload"; 

        try {
            // Create a URL object
            URL url = new URL(targetUrl);

            // Open a connection to the target URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP method to POST
            connection.setRequestMethod("POST");

            // Enable output and input streams
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Set the content type as "multipart/form-data"
            connection.setRequestProperty("Content-Type", "multipart/form-data");

            // Create a file input stream for the file to upload
            File file = new File(fileToUpload);
            FileInputStream fileInputStream = new FileInputStream(file);

            // Get the output stream from the connection
            OutputStream outputStream = connection.getOutputStream();

            // Write the file content to the output stream
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the file input stream and output stream
            fileInputStream.close();
            outputStream.close();

            // Get the response code from the server
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Process the response, if needed

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Error uploading file: " + e.getMessage());
        }
    }
}
