import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Collections;

import java.util.logging.Level;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * This class primarily does the calculation of
 * routes between devices. The actions will be based
 * on the devices added to a particular route.
 * The devices added here should be a subset of the ones
 * added to the NetworkDeviceManager. You shouldn't add
 * a device to the RouteManager if they aren't in the
 * NetworkDeviceManager. 
 */
public class RouteManager {
    private Map<NetworkDevice, List<NetworkDevice>> adjacencyList;
    private NetworkDeviceManager deviceManager;

    private LoggingManager logging = LoggingManager.getInstance();

    public RouteManager(NetworkDeviceManager deviceManager) {
        this.deviceManager = deviceManager;
        adjacencyList = new HashMap<>();

        try (Scanner scanner = new Scanner(new File("devices.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String sourceId = parts[0].trim();

                NetworkDevice source = deviceManager.getDevice(sourceId);

                addDevice(source);
            }
        } catch (Exception e) {
            logging.logEvent(Level.SEVERE, "An error occurred. Unable to access devices.txt " + e.getMessage());
        }
        try (Scanner scanner = new Scanner(new File("connections.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String sourceId = parts[0].trim();
                String destinationId = parts[1].trim();

                NetworkDevice source = deviceManager.getDevice(sourceId);
                NetworkDevice destination = deviceManager.getDevice(destinationId);

                addRoute(source, destination, 0);
            }
        } catch (Exception e) {
            logging.logEvent(Level.SEVERE, "An error occurred. Unable to access connections.txt " + e.getMessage());
        }
    }

    public void addDevice(NetworkDevice device) {
        if (!deviceManager.getDevices().contains(device)) {
            logging.logEvent(Level.WARNING, "Device not found in NetworkDeviceManager: " + device.getDeviceId());
            return;
        }

        adjacencyList.putIfAbsent(device, new ArrayList<>());

        logging.logEvent(Level.INFO, "Device added to RouteManager: " + device.getDeviceId());
    }

    public void removeDevice(NetworkDevice device) {
        if (!adjacencyList.containsKey(device)) {
            logging.logEvent(Level.WARNING, "Device not found in RouteManager: " + device.getDeviceId());
            return;
        }

        adjacencyList.remove(device);

        for (List<NetworkDevice> neighbors : adjacencyList.values()) {
            neighbors.remove(device);
        }

        try (Scanner scanner = new Scanner(new File("connections.txt"))) {
            String newConnections = "";
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String sourceId = parts[0].trim();
                String destinationId = parts[1].trim();

                if (sourceId.equals(device.getDeviceId()) || destinationId.equals(device.getDeviceId())) {
                    continue;
                }

                newConnections += line + "\n";
            }
            File file = new File("connections.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(newConnections);
            } catch (IOException e) {
                logging.logEvent(Level.SEVERE, "An error occurred. Unable to write to connections.txt: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            logging.logEvent(Level.SEVERE, "An error occurred. Unable to access connections.txt: " + e.getMessage());
        }

        logging.logEvent(Level.INFO, "Device removed from RouteManager: " + device.getDeviceId());
    }

    public void addRoute(NetworkDevice source, NetworkDevice destination, int weight) {
        if (!adjacencyList.containsKey(source)) {
            logging.logEvent(Level.WARNING, "Failed to add route. Both devices must be added to the RouteManager first.");
            return;
        } else if (!adjacencyList.containsKey(destination)) {
            logging.logEvent(Level.WARNING, "Failed to add route. Both devices must be added to the RouteManager first.");
            return;
        }

        if (!adjacencyList.get(source).contains(destination)) {
            adjacencyList.get(source).add(destination);
            adjacencyList.get(destination).add(source);

            if (weight != 0) {
                File file = new File("connections.txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.write(source.getDeviceId() + "," + destination.getDeviceId());
                    writer.newLine();
                } catch (IOException e) {
                    logging.logEvent(Level.SEVERE, "An error occurred. Unable to write to connections.txt: " + e.getMessage());
                }
            }
        }

        logging.logEvent(Level.INFO, "Route added between " + source.getDeviceId() + " and " + destination.getDeviceId());
    }

    public List<NetworkDevice> getOptimalRoute(NetworkDevice source, NetworkDevice destination) {
        if (!adjacencyList.containsKey(source)) {
            logging.logEvent(Level.WARNING, "Source device not found in RouteManager.");
            return new ArrayList<>();
        } else if (!adjacencyList.containsKey(destination)) {
            logging.logEvent(Level.WARNING, "Destination device not found in RouteManager.");
            return new ArrayList<>();
        }

        Queue<NetworkDevice> queue = new LinkedList<>();
        Set<NetworkDevice> visited = new HashSet<>();
        Map<NetworkDevice, NetworkDevice> previous = new HashMap<>();

        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            NetworkDevice current = queue.poll();

            if (current.equals(destination)) {
                break;
            }

            
            for (NetworkDevice neighbor : adjacencyList.get(current)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    previous.put(neighbor, current);

                }
            }
        }

        List<NetworkDevice> path = new ArrayList<>();
        for (NetworkDevice at = destination; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        if (path.isEmpty() || !path.get(0).equals(source)) {
            logging.logEvent(Level.WARNING, "No path found between source: " + source.getDeviceId() + " and destination: " + destination.getDeviceId());
            return new ArrayList<>();
        }

        logging.logEvent(Level.INFO, "Optimal route found between " + source.getDeviceId() + " and " + destination.getDeviceId());
        return path;
    }
}

