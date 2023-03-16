package utils;

import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Properties;


/**
 * 
 * @author malicklayesy
 *
 */
public class HttpConnector {

    public static HttpURLConnection connectTo(URL url) throws IOException, ProtocolException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }

    public static String fetchApiKey() {
        Properties properties = utils.StringUtils.loadPropertiesFromInfoAcces("src/main/java/InfoAccesConfig.properties");
        return properties.getProperty("apiKey");
    }

    public static String baseUrl() {
        Properties properties = utils.StringUtils.loadPropertiesFromInfoAcces("src/main/java/InfoAccesConfig.properties");
        return properties.getProperty("baseUrl");
    }

    private static Properties loadPropertiesFromInfoAcces() {
        Properties properties = new Properties();
        try {
            FileReader reader = new FileReader("src/main/java/InfoAccesConfig.properties");
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
