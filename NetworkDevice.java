import java.util.Scanner;
import java.util.logging.Level;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class NetworkDevice {
    LoggingManager logging = LoggingManager.getInstance();

    private String deviceId;
    private String deviceType;
    private DeviceConfiguration deviceConfig;

    public NetworkDevice(String deviceId, String deviceType, DeviceConfiguration deviceConfig) {
        deviceId = deviceId.replace(" ", "");
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceConfig = deviceConfig;

        logging.logEvent(Level.INFO, "Added device with ID: " + deviceId + ", Type: " + deviceType + ", Config: " + deviceConfig.getConfigInterface() + ", " + deviceConfig.getConfigMAC() + ", " + deviceConfig.getConfigIPV4() + ", " + deviceConfig.getConfigSubnet());
    }
    public NetworkDevice(String deviceId, String deviceType) {
        deviceId = deviceId.replace(" ", "");
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceConfig = new DeviceConfiguration("");

        logging.logEvent(Level.INFO, "Added device with ID: " + deviceId + ", Type: " + deviceType);
    }
    public String getDeviceId() {
        return deviceId;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public DeviceConfiguration getDeviceConfig() {
        return deviceConfig;
    }
    public void setDeviceConfig(DeviceConfiguration config) {
        this.deviceConfig = config;

        try {
            Scanner scanner = new Scanner(new File("devices.txt"));
            String tempFile = "";
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                
                String id = parts[0].trim();
                if (id.equals(deviceId) && config.getConfigMAC() != "") {
                    tempFile += parts[0] + ", " + parts[1] + ", Config:{" + config.getConfigInterface() + "; " + config.getConfigMAC() + "; " + config.getConfigIPV4() + "; " + config.getConfigSubnet() + "} \n";
                } else {
                    tempFile += line + "\n";
                }
            }
            scanner.close();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("devices.txt"))) {
                writer.write(tempFile);
            } catch (IOException e) {
                logging.logEvent(Level.SEVERE, "Error saving devices to file: " + e.getMessage());
            }
        } catch (Exception e) {
            logging.logEvent(Level.SEVERE, "An error occurred. Unable to access devices.txt " + e.getMessage());
        }

        logging.logEvent(Level.INFO, "Device configured. Device ID: " + deviceId + ", Config: " + deviceConfig.getConfigInterface() + ", " + deviceConfig.getConfigMAC() + ", " + deviceConfig.getConfigIPV4() + ", " + deviceConfig.getConfigSubnet());
    }

    public String toFileString() {
        String base = deviceId + ", " + deviceType;
        if (deviceConfig.getConfigInterface() != "") {
            base += ", Config:{Interface=" + deviceConfig.getConfigInterface() + "; MAC=" + deviceConfig.getConfigMAC() + "; IPV4=" + deviceConfig.getConfigIPV4() + "; Subnet=" + deviceConfig.getConfigSubnet() + "}";
        }
        return base;
    }
}
