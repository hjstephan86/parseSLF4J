package com.parseSLF4J.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parseSLF4J.parser.papertrail.Root;

public class DownloadPapertrailArchive {

    public static void main(String[] args) throws IOException, InterruptedException {
        String token = "3lPbsIymQL0vTyDC6x9"; // Replace with your actual token

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://papertrailapp.com/api/v1/archives/"))
                .header("X-Papertrail-Token", token)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper om = new ObjectMapper();
        Root[] root = om.readValue(response.body(), Root[].class);

        for (int i = root.length - 1; i >= 0; i--) {
            System.out.println(root[i].filename);
            System.out.println(root[i]._links.download.href);

            downloadGZipArchive(token, root[i].filename, root[i]._links.download.href);
            printGZipArchive(root[i].filename);
            deleteGZipArchive(root[i].filename);
            // System.out.println();
        }
    }

    public static void downloadGZipArchive(String token, String filename, String url)
            throws IOException, InterruptedException {
        // Build the command with arguments
        ProcessBuilder processBuilder = new ProcessBuilder("curl", "--no-include", "-o", filename,
                "-L", "-H",
                "X-Papertrail-Token: " + token, url);

        // Start the process
        Process process = processBuilder.start();

        // Wait for the process to finish
        process.waitFor();

        // Handle any potential errors
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            System.err.println("Error downloading archive: " + exitCode + ", " + filename);
        } else {
            // System.out.println("Downloaded archive using curl command.");
        }
    }

    private static void printGZipArchive(String filename) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("gunzip", filename);

        // Start the process
        Process process = processBuilder.start();

        // Wait for the process to finish
        process.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Handle any potential errors
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            System.err.println("Error unzipping archive: " + exitCode + ", " + filename);
        }
        reader.close();

        reader = new BufferedReader(new FileReader(filename.substring(0, filename.length() - 3)));
        line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            // Read next line
            line = reader.readLine();
        }
        reader.close();
    }

    private static void deleteGZipArchive(String filename) {
        File f = new File(filename.substring(0, filename.length() - 3));
        f.delete();
    }
}