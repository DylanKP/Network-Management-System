import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;

import java.util.ArrayList;
import java.util.List;

import java.io.FileWriter;
import java.io.IOException;




/**
 * This class acts as a manager class through which
 * the devices represented by NetworkDevice shall be
 * maintained. This class shall allow adding and
 * removing of devices, and also set configuration
 * for each device added to it. Any other class should use
 * this class to get the latest set of devices maintained
 * by the system.
 * Note: The list of devices should not be held in any
 * other class.
 */
public class NetworkDeviceManager {
    private List<NetworkDevice> devices;
    private LoggingManager logging = LoggingManager.getInstance();


    public NetworkDeviceManager() {
        devices = new ArrayList<>();

        // Load devices from file
        devices.clear();
        try (Scanner scanner = new Scanner(new File("devices.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",", 3);
                String deviceId = parts[0].trim();
                String deviceType = parts[1].trim();
                String configString = parts.length > 2 ? parts[2].trim() : "";
                devices.add(new NetworkDevice(deviceId, deviceType, new DeviceConfiguration(configString)));
            }
        } catch (FileNotFoundException e) {
            logging.logEvent(Level.INFO, "Devices file not found. Starting fresh.");

            try {
                File myObj = new File("devices.txt");
                if (myObj.createNewFile()) {
                    logging.logEvent(Level.INFO, "File created: " + myObj.getName());
                }
            } catch (IOException ex) {
                logging.logEvent(Level.SEVERE, "An error occurred. " + ex.getMessage());
            }
        }
    }


    public void addDevice(NetworkDevice device) {
        for (NetworkDevice deviceInList : devices) {
            if (deviceInList.getDeviceId().equals(device.getDeviceId())) {
                logging.logEvent(Level.WARNING, "Device with ID " + device.getDeviceId() + " already exists.");
                return;
            }
        }

        //add device
        devices.add(device);

        //write to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("devices.txt"))) {
            for (NetworkDevice netDevice : devices) {
                writer.write(netDevice.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            logging.logEvent(Level.SEVERE, "Error saving devices to file: " + e.getMessage());
        }
    }


    public void removeDevice(String deviceId) {
        boolean removed = devices.removeIf(device -> device.getDeviceId().equals(deviceId));
        if (removed) {

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("devices.txt"))) {
                for (NetworkDevice netDevice : devices) {
                    writer.write(netDevice.toFileString());
                    writer.newLine();
                }
            } catch (IOException e) {
                logging.logEvent(Level.SEVERE, "Error saving devices to file: " + e.getMessage());
            }

            logging.logEvent(Level.INFO, "Device: " + deviceId + " removed successfully from NetworkDeviceManager!");
        } else {
            logging.logEvent(Level.WARNING, "Device with ID " + deviceId + " not found.");
        }
    }


    public void configureDevice(String deviceId, String configString) {
        for (NetworkDevice device : devices) {
            if (device.getDeviceId().equals(deviceId)) {
                device.setDeviceConfig(new DeviceConfiguration(configString));

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("devices.txt"))) {
                    for (NetworkDevice netDevice : devices) {
                        writer.write(netDevice.toFileString());
                        writer.newLine();
                    }
                } catch (IOException e) {
                    logging.logEvent(Level.SEVERE, "Error saving devices to file: " + e.getMessage());
                }

                logging.logEvent(Level.INFO, "Device configured: " + deviceId);
                return;
            }
        }
        logging.logEvent(Level.WARNING, "Device with ID " + deviceId + " not found.");
    }


    public List<NetworkDevice> getDevices() {
        return devices;
    }
    
    public NetworkDevice getDevice(String deviceId) {
        for (NetworkDevice device : devices) {
            if (device.getDeviceId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }
}


