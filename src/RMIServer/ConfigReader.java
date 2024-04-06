package RMIServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class is used to read the configuration file required to start the cluster and coordinators
 * within that cluster.
 */
public class ConfigReader {
  public Map<String, String> readConfig(String filePath) {
    Properties properties = new Properties();
    Map<String, String> configMap = new HashMap<>();

    try {
      properties.load(getClass().getResourceAsStream(filePath));
      for (String key : properties.stringPropertyNames()) {
        String value = properties.getProperty(key);
        configMap.put(key, value);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return configMap;
  }
}
