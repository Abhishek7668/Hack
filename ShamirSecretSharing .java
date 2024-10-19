import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ShamirSecretSharing {

    // Function to decode a value from a given base
    private static long decodeValue(int base, String value) {
        return Long.parseLong(value, base);
    }

    // Lagrange interpolation to find the polynomial and its constant term
    private static double lagrangeInterpolation(List<Integer> xValues, List<Double> yValues, int x) {
        double total = 0;
        int k = xValues.size();

        for (int i = 0; i < k; i++) {
            double term = yValues.get(i);
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term *= (x - xValues.get(j)) / (double)(xValues.get(i) - xValues.get(j));
                }
            }
            total += term;
        }
        return total;
    }

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Read the JSON input from a file (input.json)
            JsonNode data = objectMapper.readTree(new File("input.json"));

            JsonNode keys = data.get("keys");
            int n = keys.get("n").asInt();
            int k = keys.get("k").asInt();

            List<Integer> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            // Decode the roots
            for (int i = 1; i <= n; i++) {
                JsonNode root = data.get(String.valueOf(i));
                int base = root.get("base").asInt();
                String value = root.get("value").asText();
                int x = i;
                double y = decodeValue(base, value);

                xValues.add(x);
                yValues.add(y);
            }

            // We only need k points for the polynomial
            List<Integer> xPoints = xValues.subList(0, k);
            List<Double> yPoints = yValues.subList(0, k);

            // Calculate the constant term c (f(0))
            double c = lagrangeInterpolation(xPoints, yPoints, 0);

            // Output the result
            System.out.println("The constant term (secret) c is: " + (long) c);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
