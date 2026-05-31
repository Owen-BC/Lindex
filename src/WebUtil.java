import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class WebUtil {

    // Creates a buffer reader from a valid URL
    public static BufferedReader Create_Buffer_Reader_From_URL(String URL, String User_Agent) {
        URL robots_txt;
        URLConnection connection;
        BufferedReader in;

        try {
            robots_txt = new URI(URL).toURL();
            connection = robots_txt.openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    User_Agent
            );
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
        } catch (IOException e) {
            IO.println(String.format(e.toString()));
            return null;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return in;
    }
}
