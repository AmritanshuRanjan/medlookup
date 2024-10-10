import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String DATA_FILE = "data.txt"; // Path to your data file

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/search", new SearchHandler());
        server.createContext("/save", new SaveHandler());
        server.createContext("/getData", new GetDataHandler()); // New endpoint
        server.createContext("/saveAll", new SaveAllHandler()); // New endpoint
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }

    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = generateResponseForSymptom(exchange.getRequestURI().getPath().split("/")[2]);
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static class SaveHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                StringBuilder requestBody = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                String jsonData = requestBody.toString();
                System.out.println("Received data: " + jsonData);

                // Save as plain text
                String[] fields = jsonData.replaceAll("[{}\"]", "").split(",");
                String symptoms = "", disease = "", medicine = "", dosage = "";

                for (String field : fields) {
                    String[] keyValue = field.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();
                        switch (key) {
                            case "symptoms": symptoms = value; break;
                            case "disease": disease = value; break;
                            case "medicine": medicine = value; break;
                            case "dosage": dosage = value; break;
                        }
                    }
                }

                String plainText = String.format("%s, %s, %s, %s", symptoms, disease, medicine, dosage);
                appendDataToFile(plainText);

                // Respond with success
                String response = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/plain\r\n\r\n"
                        + "Data saved successfully";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    // New handler for getting all data
    static class GetDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = readAllDataAsJson();
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    // New handler for saving all data
    static class SaveAllHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCorsHeaders(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                StringBuilder requestBody = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                String jsonData = requestBody.toString();
                saveAllData(jsonData);

                String response = "All data saved successfully.";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    private static String generateResponseForSymptom(String symptom) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("["); // Start of JSON array
        List<String[]> diseaseData = readDataFromFile();
        boolean found = false;

        for (String[] record : diseaseData) {
            if (record[0].equalsIgnoreCase(symptom)) {
                responseBuilder.append(String.format("{\"disease\":\"%s\", \"medicine\":\"%s\", \"dosage\":\"%s\"},",
                        record[1], record[2], record[3]));
                found = true;
            }
        }

        if (found) {
            responseBuilder.setLength(responseBuilder.length() - 1); // Remove last comma
        }
        responseBuilder.append("]"); // End of JSON array

        return responseBuilder.toString();
    }

    private static List<String[]> readDataFromFile() {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 4) {
                    records.add(parts);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
        return records;
    }

    private static String readAllDataAsJson() {
        List<String[]> records = readDataFromFile();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (String[] record : records) {
            jsonBuilder.append(String.format("{\"symptoms\":\"%s\", \"disease\":\"%s\", \"medicine\":\"%s\", \"dosage\":\"%s\"},",
                    record[0], record[1], record[2], record[3]));
        }
        if (records.size() > 0) {
            jsonBuilder.setLength(jsonBuilder.length() - 1); // Remove last comma
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    private static void saveAllData(String jsonData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) { // Open in append mode
            jsonData = jsonData.replaceAll("[\\[\\]{}\"]", ""); // Clean up JSON format
            String[] records = jsonData.split("\\},\\{"); // Split by record
            
            for (String record : records) {
                record = record.replaceAll("[{}]", ""); // Remove curly braces
                String[] fields = record.split(","); // Split by comma
                
                // Check for 4 fields to match symptoms, disease, medicine, dosage
                if (fields.length == 4) {
                    String[] cleanedFields = new String[4];
                    for (int i = 0; i < fields.length; i++) {
                        cleanedFields[i] = fields[i].split(":")[1].trim(); // Get the value part
                    }
                    writer.write(String.join(", ", cleanedFields));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void appendDataToFile(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(data);
            writer.newLine(); // Add a new line after each entry
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
