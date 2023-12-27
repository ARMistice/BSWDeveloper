package de.brettspielwelt.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class FileServer {
    public static void main(String[] args) throws IOException {
        // Set the port for the server
        int port = 8000;

        // Create the HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Set the handler for file uploads
        server.createContext("/upload", new FileUploadHandler());

        // Set the handler for file downloads
        server.createContext("/download", new FileDownloadHandler());

        // Start the server
        server.start();

        System.out.println("File Server is running on port " + port);
    }

    static class FileUploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                // Get the file name from the request headers
                String fileName = "game"+System.currentTimeMillis()+".zip"; //extractFileName(exchange.getRequestHeaders().getFirst("Content-Disposition"));

                // Save the uploaded file
                InputStream inputStream = exchange.getRequestBody();
                FileOutputStream outputStream = new FileOutputStream(fileName);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();

                // Send the response
                String response = "File uploaded successfully: " + fileName;
                exchange.sendResponseHeaders(200, response.length());
                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(response.getBytes());
                responseBody.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class FileDownloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                // Get the file name from the request URL
                String fileName = exchange.getRequestURI().getPath().substring("/download/".length());

                // Check if the file exists
                File file = new File(fileName);
                if (file.exists() && file.isFile()) {
                    // Set the content type as "application/octet-stream" for downloading
                    exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");

                    // Set the content disposition to attachment to trigger download
                    exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

                    // Send the file content as response
                    exchange.sendResponseHeaders(200, file.length());
                    OutputStream responseBody = exchange.getResponseBody();
                    Files.copy(file.toPath(), responseBody);
                    responseBody.close();
                } else {
                    exchange.sendResponseHeaders(404, -1); // File Not Found
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    private static String extractFileName(String headerValue) {
        String[] parts = headerValue.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("filename")) {
                String fileName = part.substring(part.indexOf('=') + 1).trim();
                return fileName.substring(1, fileName.length() - 1);
            }
        }
        return null;
    }
}
