import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String DATA_FILE = "data.txt"; // File to save and read data

    public static void main(String[] args) throws IOException {
        int port = 8080; // Port number
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String requestLine = in.readLine();
                System.out.println("Request: " + requestLine);

                if (requestLine != null) {
                    String[] parts = requestLine.split(" ");
                    String method = parts[0];

                    // Handle CORS preflight requests
                    if ("OPTIONS".equals(method)) {
                        out.println("HTTP/1.1 204 No Content\r\n"
                                + "Access-Control-Allow-Origin: *\r\n"
                                + "Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n"
                                + "Access-Control-Allow-Headers: Content-Type\r\n\r\n");
                        continue;
                    }

                    // Respond with CORS header
                    String corsHeader = "Access-Control-Allow-Origin: *\r\n";

                    if (parts.length >= 3) {
                        if (parts[0].equals("GET") && parts[1].startsWith("/search/")) {
                            String symptom = parts[1].substring(8); // Extract symptom from URL
                            String response = generateResponseForSymptom(symptom);
                            out.println("HTTP/1.1 200 OK\r\n"
                                    + corsHeader
                                    + "Content-Type: application/json\r\n\r\n"
                                    + response);
                        } else if (parts[0].equals("POST") && parts[1].equals("/save")) {
                            // Handle saving data
                            StringBuilder requestBody = new StringBuilder();
                            String line;
                            while (!(line = in.readLine()).isEmpty()) {
                                // Read headers, ignore them for simplicity
                            }

                            while (in.ready()) {
                                requestBody.append((char) in.read());
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
                                    + corsHeader
                                    + "Content-Type: text/plain\r\n\r\n"
                                    + "Data saved successfully";
                            out.println(response);
                        } else {
                            out.println("HTTP/1.1 404 Not Found\r\n\r\n");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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

    private static void appendDataToFile(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(data);
            writer.newLine(); // Add a new line after each entry
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
