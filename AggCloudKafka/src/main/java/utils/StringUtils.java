package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;
import java.io.FileReader;
import java.util.Properties;


/**
 * 
 * @author malicklayesy
 *
 */
public class StringUtils {


    public static URL createUrl(String baseURL, String apiKey, String keyword, String country) {
        URL requiredURL = null;
        try {
            requiredURL = new URL(baseURL
                    + "/everything?q=" + keyword
                    + "&apiKey=" + apiKey
                    + "&language=" + country);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return requiredURL;
    }

    public static String readFrom(URL url) throws IOException {
        String content = "";
        Scanner sc = new Scanner(url.openStream(), StandardCharsets.UTF_8);
        while (sc.hasNext()) {
            content += sc.nextLine();
            content.replaceAll("\n", "");
        }
        sc.close();
        return content;
    }

    public static Properties loadPropertiesFromInfoAcces(String path) {
        Properties properties = new Properties();
        try {
            FileReader reader = new FileReader(path);
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
