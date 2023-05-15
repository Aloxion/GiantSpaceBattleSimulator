package gsbs.debugsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PciIdParser {
    private final HashMap<String, String> deviceNames;

    public PciIdParser(String filePath) {
        deviceNames = new HashMap<>();

        try (var stream = getClass().getResourceAsStream(filePath)) {
            assert stream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            String currentVendor = "";
            while ((line = reader.readLine()) != null) {
                // Comment
                if (line.startsWith("#")) {
                    continue;
                }

                // Subdevice
                if (line.startsWith("\t\t")) {
                    continue;
                }

                // Device
                if (line.startsWith("\t")) {
                    List<String> tokens = Arrays.stream(line.trim().split("\\s+")).collect(Collectors.toList());
                    if (tokens.size() >= 2) {
                        String deviceId = tokens.remove(0);
                        String deviceName = String.join(" ", tokens);
                        deviceNames.put(currentVendor + ":" + deviceId, deviceName);
                    }
                    continue;
                }

                // Vendor
                List<String> tokens = Arrays.stream(line.trim().split("\\s+")).collect(Collectors.toList());
                if (tokens.size() >= 2) {
                    currentVendor = tokens.get(0);
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String lookupName(String vendorId, String deviceId) {
        return deviceNames.get(vendorId + ":" + deviceId);
    }
}