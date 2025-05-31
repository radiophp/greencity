package greencity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Loads the device CSV generated earlier.
 * Expected header: DeviceName,Category,CostTRY,EnergySaved_kWh,SustainabilityScore
 */
public class DatasetLoader {
    private static final Logger log = Logger.getLogger(DatasetLoader.class.getName());

    public static List<Device> load(Path csvPath) throws IOException {
        log.info("Loading dataset from " + csvPath.toAbsolutePath());
        List<String> lines = Files.readAllLines(csvPath);
        if (lines.isEmpty()) throw new IOException("CSV is empty: " + csvPath);

        List<Device> devices = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) { // skip header
            String[] tok = lines.get(i).split(",", -1);
            if (tok.length < 5) continue; // skip malformed rows
            Device d = new Device(
                    tok[0].trim(),
                    tok[1].trim(),
                    Double.parseDouble(tok[2]),
                    Double.parseDouble(tok[3]),
                    Integer.parseInt(tok[4])
            );
            devices.add(d);
        }
        log.info("Loaded " + devices.size() + " devices.");
        return devices;
    }
}