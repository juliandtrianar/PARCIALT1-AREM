package edu.eci.arep;

import java.io.*;
import java.net.*;
import java.util.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FacadeService {
    private static final int PORT = 8080;
    private static final String BACKEND_HOST = "localhost";
    private static final int BACKEND_PORT = 9000;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Fachada lista en puerto " + PORT);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream rawOut = clientSocket.getOutputStream();
             PrintWriter out = new PrintWriter(rawOut)) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;
            StringTokenizer tokens = new StringTokenizer(requestLine);
            String method = tokens.nextToken();
            String path = tokens.nextToken();

            if (path.equals("/") || path.startsWith("/index.html")) {
                sendFileResponse(out, rawOut, "index.html");
            } else if (path.startsWith("/setkv") || path.startsWith("/getkv")) {
                String backendResponse = forwardToBackend(path);
                sendJsonResponse(out, backendResponse);
            } else {
                sendNotFound(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFileResponse(PrintWriter out, OutputStream rawOut, String filename) throws IOException {
        File file = new File("src/main/resources/public/" + filename);
        if (!file.exists()) {
            sendNotFound(out);
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        byte[] data = fis.readAllBytes();
        fis.close();

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + data.length);
        out.println();
        out.flush();
        rawOut.write(data);
        rawOut.flush();
    }

    private static void sendJsonResponse(PrintWriter out, String body) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + body.length());
        out.println();
        out.println(body);
    }

    private static void sendNotFound(PrintWriter out) {
        String body = "{\"error\":\"not_found\"}";
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + body.length());
        out.println();
        out.println(body);
    }

    private static String forwardToBackend(String path) throws IOException {
        Socket backendSocket = new Socket(BACKEND_HOST, BACKEND_PORT);
        PrintWriter backendOut = new PrintWriter(backendSocket.getOutputStream(), true);
        BufferedReader backendIn = new BufferedReader(new InputStreamReader(backendSocket.getInputStream()));

        backendOut.println("GET " + path + " HTTP/1.1");
        backendOut.println("Host: " + BACKEND_HOST);
        backendOut.println("Connection: close");
        backendOut.println();

        StringBuilder response = new StringBuilder();
        String line;
        boolean jsonStarted = false;
        while ((line = backendIn.readLine()) != null) {
            if (line.isEmpty()) {
                jsonStarted = true;
                continue;
            }
            if (jsonStarted) response.append(line);
        }

        backendSocket.close();
        return response.toString();
    }
}
