import java.util.logging.Level;

public class DeviceConfiguration {
    LoggingManager logging = LoggingManager.getInstance();

    private String configInterface = "";
    private String configMAC = "";
    private String configIPV4 = "";
    private String configSubnet = "";

    public DeviceConfiguration(String config) {
        try {
            String[] parts = config.split(";");


            String interfaceName = parts[0].replace("Config:{Interface=", "").replace("Config:{Interface:", "").trim();

            String mac = parts[1].replace("MAC=", "").replace("MAC:", "").trim();
            String macREGEX = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

            String ipv4 = parts[2].replace("IPV4=", "").replace("IPV4:", "").trim();
            String ipv4REGEX = "^([0-9]{1,3}(\\.[0-9]{1,3}){3})$";

            String subnet = parts[3].replace("Subnet=", "").replace("Subnet:", "").replace("}", "").trim();
            String subnetREGEX = "^((255|254|252|248|240|224|192|128|0)\\.0\\.0\\.0|255\\.(255|254|252|248|240|224|192|128|0)\\.0\\.0|255\\.255\\.(255|254|252|248|240|224|192|128|0)\\.0|255\\.255\\.255\\.(255|254|252|248|240|224|192|128|0))$"; 

            if (!mac.matches(macREGEX) || !ipv4.matches(ipv4REGEX) || !subnet.matches(subnetREGEX)) {
                throw new Exception("Invalid Configuration");
            }

            this.configInterface = interfaceName;
            this.configMAC = mac;
            this.configIPV4 = ipv4;
            this.configSubnet = subnet;

            logging.logEvent(Level.INFO, "Configured device with Interface: " + this.configInterface + ", MAC: " + this.configMAC + ", IPV4: " + this.configIPV4 + ", Subnet: " + this.configSubnet);
        } catch (Exception e) {
            this.configInterface = "";
            this.configMAC = "";
            this.configIPV4 = "";
            this.configSubnet = "";

            logging.logEvent(Level.INFO, "Config N/A. Saved device with empty configuration");
        }
    }

    public String getConfigInterface() {
        return configInterface;
    }
    public String getConfigMAC() {
        return configMAC;
    }
    public String getConfigIPV4() {
        return configIPV4;
    }
    public String getConfigSubnet() {
        return configSubnet;
    }
}
