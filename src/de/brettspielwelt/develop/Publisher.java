package de.brettspielwelt.develop;
import java.io.*;
import java.util.zip.*;

public class Publisher {
    public static void publish() {
        String folderPath = "src/de/brettspielwelt/game"; // Replace with the path to your folder
        String zipFilePath = "publish.zip"; // Replace with the path to your ZIP file

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            File folder = new File(folderPath);
            addFolderToZip(folder, "", zipOutputStream);
            addFolderToZip(new File("assets"), "assets/", zipOutputStream);

            System.out.println("Folder added to ZIP file successfully!");
        } catch (IOException e) {
            System.out.println("Error adding folder to ZIP file: " + e.getMessage());
        }
    }

    private static void addFolderToZip(File folder, String parentPath, ZipOutputStream zipOutputStream) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentPath + file.getName() + "/", zipOutputStream);
            } else {
                byte[] buffer = new byte[1024];
                FileInputStream fileInputStream = new FileInputStream(file);
                zipOutputStream.putNextEntry(new ZipEntry(parentPath + file.getName()));
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
        }
    }
}