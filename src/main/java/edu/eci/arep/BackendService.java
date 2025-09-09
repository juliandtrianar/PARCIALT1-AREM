package edu.eci.arep;

import java.io.*;
import java.net.*;
import java.util.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class BackendService {
    private static final int PORT = 9000;
    private static final Map<String, String> kvStore = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Backend listo en puerto " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            StringTokenizer tokens = new StringTokenizer(requestLine);
            String method = tokens.nextToken();
            String path = tokens.nextToken();

            if (method.equals("GET") && path.startsWith("/setkv")) {
                Map<String, String> queryParams = parseQuery(path);
                String key = queryParams.get("key");
                String value = queryParams.get("value");

                if (key != null && value != null) {
                    kvStore.put(key, value);
                    sendResponse(out, 200, "{\"key\":\"" + key + "\", \"value\":\"" + value + "\", \"status\":\"created\"}");
                } else {
                    sendResponse(out, 400, "{\"error\":\"missing_parameters\"}");
                }
            } else if (method.equals("GET") && path.startsWith("/getkv")) {
                Map<String, String> queryParams = parseQuery(path);
                String key = queryParams.get("key");
                String value = kvStore.get(key);

                if (value != null) {
                    sendResponse(out, 200, "{\"key\":\"" + key + "\", \"value\":\"" + value + "\"}");
                } else {
                    sendResponse(out, 404, "{\"error\":\"key_not_found\", \"key\":\"" + key + "\"}");
                }
            } else {
                sendResponse(out, 404, "{\"error\":\"not_found\"}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(PrintWriter out, int statusCode, String body) {
        out.println("HTTP/1.1 " + statusCode + " OK");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + body.length());
        out.println();
        out.println(body);
    }

    private static Map<String, String> parseQuery(String path) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new HashMap<>();
        int idx = path.indexOf("?");
        if (idx > 0 && idx < path.length() - 1) {
            String[] pairs = path.substring(idx + 1).split("&");
            for (String pair : pairs) {
                int eq = pair.indexOf("=");
                if (eq > 0) {
                    String key = URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                    queryPairs.put(key, value);
                }
            }
        }
        return queryPairs;
    }
}
