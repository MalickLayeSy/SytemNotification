package aggregateur;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;




import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import static utils.HttpConnector.*;

public class ProducerAgg{


    //UTILE 1
    public static URL createUrl(String baseURL, String apiKey, String keyword, String country) {
        URL requiredURL = null;
        try {
            requiredURL = new URL(baseURL
                    + "/everything?q=" + keyword
                    + "&sortBy=popularity"
                    + "&apiKey=" + apiKey);

            System.out.println("-------- "+ requiredURL );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return requiredURL;
    }

    //UTILE 2
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


    public static String fetchAllArticlesByCountryAndTopic(String forCountry, String onTopic) {
        final String NEWS_URL = baseUrl();
        final String API_KEY = fetchApiKey();
        final URL url = createUrl(NEWS_URL, API_KEY, onTopic, forCountry);

        String responseAsString = "";

        if (onTopic.isEmpty() || forCountry.isEmpty()) {
            throw new IllegalArgumentException("Params should not be empty.");
        } else {
            try {
                HttpURLConnection connection = connectTo(url);
                int responsecode = connection.getResponseCode();
                if (responsecode != 200) {
                    throw new IllegalStateException("Request failed - response code: " + responsecode);
                } else {
                    responseAsString = readFrom(url);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseAsString;
    }


    public static void main(final String[] args) throws IOException {

        Boolean checker = true;
        int cpt =2023;

        // Load producer configuration settings from a local file
        //getting-started.properties
        final Properties props = utils.StringUtils.loadPropertiesFromInfoAcces("src/main/java/getting-started.properties");
        final String topic = "Finance";

        String dataApi= fetchAllArticlesByCountryAndTopic("us", "apple");

        // Créer un objet ObjectMapper pour parser le JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Parser le contenu JSON
        JsonNode jsonNode = objectMapper.readTree(dataApi);
        
        //System.out.println(jsonNode);
         
       JsonNode itemArticles = jsonNode.get("articles");

       for( int i=0 ; i<itemArticles.size();i++){
            JsonNode itemArticle = itemArticles.get(i);
            //System.out.println(itemArticle) ;
            System.out.println("=============================================================") ;
            String idItemArticle =  "itemArticle"+cpt;
            String name          =  itemArticle.get("source").get("name").asText();
            String author        =  itemArticle.get("author").asText();
            String title         =  itemArticle.get("title").asText();
            String description   =  itemArticle.get("description").asText();
            String url           =  itemArticle.get("url").asText();
            String urlToImage    =  itemArticle.get("urlToImage").asText();
            String publishedAt   =  itemArticle.get("publishedAt").asText();
            String content       =  itemArticle.get("content").asText();
            
             try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					return;
				}
            ItemArticle  art = null;
            if(urlToImage != null)
                art = new ItemArticle(idItemArticle,name,author,title,description,url,urlToImage,publishedAt,content);
                
            
           try (final Producer<String, String> producer = new KafkaProducer<>(props)) {
            String  user ="MLS";
                producer.send(
                        new ProducerRecord<>(topic, user, art.toString()),
                        (event, ex) -> {
                            if (ex != null)
                                ex.printStackTrace();
                            else
                               System.out.printf("Produced event to topi "+   topic, user);
                        });
            }

            cpt++;

            System.out.println("=============================================================") ;
            
            System.out.println(art);
       }




    }

 

    // We'll reuse this function to load properties from the Consumer as well
    public static Properties loadConfig(final String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        return cfg;
    }
}

