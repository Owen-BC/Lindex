import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.StringEscapeUtils;

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
            } catch(java.net.URISyntaxException e){
                IO.println("URI failed to parse: " + e);
            }
        }
        return output;
    }

    public static List<String> ParseHTMLForLinksUsage(String HTML){
        return null;
    }
}
