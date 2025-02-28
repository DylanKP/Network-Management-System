import java.util.Scanner;
import java.util.List;

/**
 * This is the primary class of the system.
 * It will be used to launch the system and perform
 * the necessary actions, like adding devices,
 * removing devices, getting optimal route between
 * devices, filtering and searching for devices,
 * creating alerts etc. 
 * NOTE: DO NOT MOVE THIS CLASS TO ANY PACKAGE.
 *
 */

 public class NMS {
    public static void main(String[] args) {
        NetworkDeviceManager deviceManager = new NetworkDeviceManager();
        RouteManager routeManager = new RouteManager(deviceManager);

        Scanner scanner = new Scanner(System.in); // Use a single scanner for all input
        int choice;

        System.out.println("Loaded all devices from devices.txt!");
        System.out.println("Loaded all connections from connections.txt!");

        do {
            System.out.println();
            System.out.println("--- Network Management System ---");
            System.out.println("1. Add Device");
            System.out.println("2. Remove Device");
            System.out.println("3. Add Route");
            System.out.println("4. Get Optimal Route");
            System.out.println("5. Configure Devices");
            System.out.println("6. List Devices");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline left by nextInt()
            } catch (Exception e) {
                scanner.nextLine(); // Consume invalid input
                choice = 0;
            }

            switch (choice) {
                case 1:
                    // Add Device
                    System.out.println("Enter Device ID:");
                    String deviceId = scanner.nextLine();

                    System.out.println("Enter Device Type:");
                    String deviceType = scanner.nextLine();

                    System.out.println("Configure Device? y/n");
                    String deviceConfig = scanner.nextLine();

                    if (deviceConfig.equals("y")) {
                        System.out.println("Enter Configuration Interface:");
                        String deviceConfigInterface = scanner.nextLine();

                        System.out.println("Enter Configuration MAC:");
                        String deviceConfigMAC = scanner.nextLine();

                        System.out.println("Enter Configuration IPV4:");
                        String deviceConfigIP = scanner.nextLine();

                        System.out.println("Enter Configuration Subnet:");
                        String deviceConfigSubnet = scanner.nextLine();

                        String configString = "Config:{Interface=" + deviceConfigInterface + "; MAC=" + deviceConfigMAC + "; IPV4=" + deviceConfigIP + "; Subnet=" + deviceConfigSubnet + "}";

                        deviceManager.addDevice(new NetworkDevice(deviceId, deviceType, new DeviceConfiguration(configString)));
                        routeManager.addDevice(deviceManager.getDevice(deviceId));
                    } else {
                        deviceManager.addDevice(new NetworkDevice(deviceId, deviceType));
                        routeManager.addDevice(deviceManager.getDevice(deviceId));
                    }
                    break;
                case 2:
                    // Remove Device
                    System.out.println("Enter Device ID:");
                    String removeDeviceId = scanner.nextLine();

                    routeManager.removeDevice(deviceManager.getDevice(removeDeviceId));
                    deviceManager.removeDevice(removeDeviceId);
                    break;
                case 3:
                    // Add Route
                    System.out.println("Enter Source Device ID:");
                    String sourceId = scanner.nextLine();

                    System.out.println("Enter Destination Device ID:");
                    String destinationId = scanner.nextLine();

                    NetworkDevice source = deviceManager.getDevice(sourceId);
                    NetworkDevice destination = deviceManager.getDevice(destinationId);

                    routeManager.addRoute(source, destination, 1);
                    break;
                case 4:
                    // Get Optimal Route
                    System.out.println("Enter Source Device ID:");
                    String optimalSourceId = scanner.nextLine();

                    System.out.println("Enter Destination Device ID:");
                    String optimalDestinationId = scanner.nextLine();

                    NetworkDevice optimalSource = deviceManager.getDevice(optimalSourceId);
                    NetworkDevice optimalDestination = deviceManager.getDevice(optimalDestinationId);

                    if (optimalSource != null && optimalDestination != null) {
                        List<NetworkDevice> path = routeManager.getOptimalRoute(optimalSource, optimalDestination);

                        if (path != null) {
                            System.out.println("Optimal route calculated successfully! Distance: " + path.size());
                            System.out.println("Route: " );
                            int i = 1;
                            for (NetworkDevice device : path) {
                                if (device.getDeviceConfig().getConfigInterface().equals("")) {
                                    System.out.println(i + ": " + "Device ID: " + device.getDeviceId() + ", Type: " + device.getDeviceType());
                                } else {
                                    System.out.println(i + ": " + "Device ID: " + device.getDeviceId() + ", Type: " + device.getDeviceType() + ", Config: " + device.getDeviceConfig().getConfigInterface() + ", " + device.getDeviceConfig().getConfigMAC() + ", " + device.getDeviceConfig().getConfigIPV4() + ", " + device.getDeviceConfig().getConfigSubnet());
                                }
                                i++;
                            }
                        } else {
                            System.out.println("No route found between Source: " + optimalSourceId + " and Destination: " + optimalDestinationId);
                        }
                    } else {
                        System.out.println("Invalid source or destination device ID. Please check and try again.");
                    }
                    break;
                case 5:
                    // Configure Devices
                    System.out.println("Enter Device ID:");
                    String configDeviceId = scanner.nextLine();

                    NetworkDevice configDevice = deviceManager.getDevice(configDeviceId);

                    if (configDevice.getDeviceConfig().getConfigInterface().equals("")) {
                        System.out.println("Selected Device Id: " + configDevice.getDeviceId() + ", Type: " + configDevice.getDeviceType());
                    } else {
                        System.out.println("Selected Device Id: " + configDevice.getDeviceId() + ", Type: " + configDevice.getDeviceType() + ", Config: " + configDevice.getDeviceConfig().getConfigInterface() + ", " + configDevice.getDeviceConfig().getConfigMAC() + ", " + configDevice.getDeviceConfig().getConfigIPV4() + ", " + configDevice.getDeviceConfig().getConfigSubnet());
                    }

                    System.out.println("Configure Device? y/n");
                    String configureDevice = scanner.nextLine();

                    if (configureDevice.equals("y")) {
                        System.out.println("Enter Configuration Interface:");
                        String deviceConfigInterface = scanner.nextLine();

                        System.out.println("Enter Configuration MAC:");
                        String deviceConfigMAC = scanner.nextLine();

                        System.out.println("Enter Configuration IPV4:");
                        String deviceConfigIP = scanner.nextLine();

                        System.out.println("Enter Configuration Subnet:");
                        String deviceConfigSubnet = scanner.nextLine();

                        String configString = "Config:{Interface=" + deviceConfigInterface + "; MAC=" + deviceConfigMAC + "; IPV4=" + deviceConfigIP + "; Subnet=" + deviceConfigSubnet + "}";

                        configDevice.setDeviceConfig(new DeviceConfiguration(configString));
                    }
                    break;
                case 6:
                    // List Devices
                    List<NetworkDevice> devices = deviceManager.getDevices();
                    System.out.println("Devices:");
                    int i = 1;

                    for (NetworkDevice device : devices) {
                        if (device.getDeviceConfig().getConfigInterface().equals("")) {
                            System.out.println(i + ": " + "Device ID: " + device.getDeviceId() + ", Type: " + device.getDeviceType());
                        } else {
                            System.out.println(i + ": " + "Device ID: " + device.getDeviceId() + ", Type: " + device.getDeviceType() + ", Config: " + device.getDeviceConfig().getConfigInterface() + ", " + device.getDeviceConfig().getConfigMAC() + ", " + device.getDeviceConfig().getConfigIPV4() + ", " + device.getDeviceConfig().getConfigSubnet());
                        }
                        i++;
                    }
                    break;
                case 7:
                    scanner.close(); // Close the scanner when exiting
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid choice. Please only type the number of the choice.");
            }
        } while (choice != 7);
    }
}
