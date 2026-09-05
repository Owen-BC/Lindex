import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.StringEscapeUtils;

public class WebUtil {
    // Creates a buffer reader from a valid URL
    public static BufferedReader Create_Buffer_Reader_From_URL(URI URL, String User_Agent, String updated) {
        URL robots_txt;
        HttpURLConnection connection = null;
        BufferedReader in;

        try {
            robots_txt = URL.toURL();
            connection = (HttpURLConnection) robots_txt.openConnection();
            connection.setRequestMethod("GET"); // should be default, set for readability
            connection.setRequestProperty(
                    "User-Agent",
                    User_Agent
            );
            if(updated != null) {
                connection.setRequestProperty(
                        "If-Modified-Since",
                        updated
                );
            }

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            if(!checkConnectionForError(connection)){
                throw new IOException(connection.getResponseCode() + connection.getResponseMessage());
            }
            if(connection.getResponseCode() == 304) {
                IO.println("No update in file content");
                return null;
            }
        } catch (IOException e) {
            IO.println(String.format(e.toString()));
            return null;
        }
        return in;
    }

    // returns false on a 400+ error code
    private static boolean checkConnectionForError(HttpURLConnection connection) {
        try {
            int responseCode = connection.getResponseCode();
            InputStream stream;

            if (responseCode >= 400) {
                stream = connection.getErrorStream();
                String body = new String(
                        stream.readAllBytes(),
                        StandardCharsets.UTF_8
                );

                System.out.println(body);
                return false;
            }
        } catch(IOException e) {
            return false;
        }
        return true;
    }

//  Extracts all plaintext within the provided HTML and output a HashTable
    public static Hashtable<String, Integer> ParseHTMLForTextUsage(String HTML){
        Pattern removeHead = Pattern.compile("</head>");
        String[] headRemoveds = removeHead.split(HTML);
        String headRemoved = headRemoveds[headRemoveds.length - 1];
        headRemoved = headRemoved.replaceAll("(?s)<style\\b[^>]*>.*?</style>","");
        headRemoved = headRemoved.replaceAll("(?s)<script\\b[^>]*>.*?</script>","");
        boolean done = false;
        while(!done) {
            String old = headRemoved;
            headRemoved = headRemoved.replaceAll("(?s)\\{[^}]*}", "");
            done = (headRemoved.equals(old));
        }
        headRemoved = StringEscapeUtils.unescapeHtml4(headRemoved);
        Matcher text = Pattern.compile("<[^<>]*>([^<>]*)").matcher(headRemoved);
        Pattern splitWords = Pattern.compile("[^a-zA-Z0-9]+");
        List<String> words = new ArrayList<>();
        Hashtable<String, Integer> wordUsage = new Hashtable<String, Integer>();

        while(text.find()) {
            String out = text.group(1);
            List<String> split = new ArrayList<>(List.of(splitWords.split(out)));
            split.removeIf(String::isEmpty);

            if(!split.isEmpty()) {
                for (String word : split) {
                    word = word.toLowerCase();
                    if (word.isEmpty())
                        continue;
                    Integer result = wordUsage.get(word);
                    if (result == null) {
                        result = 1;
                    } else {
                        result = result + 1;
                    }
                    wordUsage.put(word, result);
                }
            }
        }
        return wordUsage;
    }

    public static Hashtable<String, List<String>> getDistinctEntryLinkLists(List<String> input){
        Hashtable<String, List<String>> output = new Hashtable<>();
        for(String link: input){
            try {
                URI thisLink = new URI(link);
                List<String> cur = output.get(thisLink.getHost());
                if(cur == null){
                    cur = new LinkedList<String>();

                }
                cur.add(link);
                output.put(thisLink.getHost(), cur);
            } catch(URISyntaxException e){
                IO.println("URI failed to parse: " + e);
            }
        }
        return output;
    }

    /** Creates a URI from a provided URL, returns null if the URI construction throws a error
     *
     * @return URI or null
     */
    public static URI createURI(String URL){
        try {
            URI url = new URI(URL);
            return url;
        }   catch (URISyntaxException e) {
            return null;
        }
    }

    /****
     *returns all data provided from a buffer reader
     * @param in valid not null buffer reader
     * @return string representing the data provided by a buffer reader and null if a IOException
     */
    public static String getAllDataFromBufferReader(BufferedReader in) {
        assert(in != null);
        String result;
        try {
            result = in.readAllAsString();
        } catch(IOException e) {
            result = null;
        }
        return result;
    }

    /***
     * Creates a http compatable string representing the provided date
     * @param date
     * @return simple date format containing the date provided
     */
    public static String generateUpdatedDate(Date date){
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static List<URI> parseHTMLForLinks(String Domain, String parseLine) {
        Pattern hrefs = Pattern.compile("href=\"([^\"]*)\"");
        Matcher matcher = hrefs.matcher(parseLine);
        String matched;
        List<URI> foundLinks = new ArrayList<>();
        while (matcher.find()) {
            String found = matcher.group(1);
            found = found.replace(" ", "%20");
            if(!(found.startsWith("http://") | found.startsWith("https://"))){
                found = found.replace(" ", "%20");
                found = "https://".concat(Domain).concat(found);
            }
            // encode the path of the link
            URI newLink;
            try {
                newLink = URI.create(found);
                foundLinks.add(newLink);
            } catch (Exception _) {
            }
        }
        return foundLinks;
    }
}
