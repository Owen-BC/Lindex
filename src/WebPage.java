import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPage {
    private URI URL;
    private String Domain;
    private String user_agent;
    private String title;
    private Hashtable<String,Integer> wordUsage;
    private Hashtable<String, Integer> linkedToByOtherPages;
    private List<String> intraDomainLinks;
    private List<String> interDomainLinks;
    private int score;
    private Date updated;
    private int threshHold;


    public WebPage(String URL, String user_agent) throws URISyntaxException, IOException, InterruptedException {
        this.URL = new URI(URL);
        this.user_agent = user_agent;
        this.Domain = this.URL.getHost();
        this.title = "NAN";
        this.wordUsage = new Hashtable<>();
        this.interDomainLinks = new ArrayList<>();
        this.intraDomainLinks = new ArrayList<>();
        this.linkedToByOtherPages = new Hashtable<>();
        this.updated = new Date();
        this.threshHold = 3;
        parsePageData();
        this.score = 0;
//        Thread.sleep(1000); // this is a stupid way to enforce one request per second dweebo
    }


    public WebPage(JSONObject data, String user_agent) throws URISyntaxException, IOException, InterruptedException {
        this.URL = new URI((String) data.get("URL"));
        this.user_agent = user_agent;
        this.title = data.get("title").toString();
        this.Domain = URL.getHost();
        this.updated = (Date) data.get("updated");
        this.score = (int) data.get("score");

        // parse hash table nonsense :(
        this.wordUsage = new Hashtable<String, Integer>();
        String rawHashTable = data.get("wordUsage").toString();
        String[] split_table = rawHashTable.split("[{,} ]+");
        for(String item: split_table) {
            if(!item.isEmpty()) {
                String[] parts = item.split("=");
                wordUsage.put(parts[0], Integer.valueOf(parts[1]));
            }
        }

        rawHashTable = null;
        split_table = null;

        this.linkedToByOtherPages = new Hashtable<String, Integer>();
        rawHashTable = data.get("wordUsage").toString();
        split_table = rawHashTable.split(",");
        for(String item: split_table) {
            String[] parts = item.split("=");
            linkedToByOtherPages.put(parts[0], Integer.valueOf(parts[1]));
        }

        rawHashTable = null;
        split_table = null;

        this.interDomainLinks = new ArrayList<>();
        rawHashTable = data.get("interDomainLinks").toString();
        split_table = rawHashTable.split(",");
        intraDomainLinks.addAll(Arrays.asList(split_table));

        rawHashTable = null;
        split_table = null;

        this.intraDomainLinks = new ArrayList<>();
        rawHashTable = data.get("intraDomainLinks").toString();
        split_table = rawHashTable.split(",");
        intraDomainLinks.addAll(Arrays.asList(split_table));
    }


    private void parsePageData() throws URISyntaxException, IOException {
        BufferedReader in;

        URL url = URL.toURL();
        URLConnection connection = url.openConnection();
        connection.setRequestProperty(
                "User-Agent",
                user_agent
        );

        in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        StringBuilder fullText = new StringBuilder();
        String curline;
        while ((curline = in.readLine()) != null) {
            fullText.append(curline);
        }

        String fulltextString = fullText.toString();
        addTitle(fulltextString);
        wordUsage = WebUtil.ParseHTMLForTextUsage(fulltextString);
        parseDomainLinks(fulltextString);
    }

    private void parseDomainLinks(String parseLine) {
        Pattern hrefs = Pattern.compile("href=\"([^\"]*)\"");
        Matcher matcher = hrefs.matcher(parseLine);
        String matched;
        while (matcher.find()) {
            String found = matcher.group(1);

            if(!(found.startsWith("http://") | found.startsWith("https://")))
                found = "https://".concat(this.Domain).concat(found);
            URI newLink;
            try {
                newLink = URI.create(found);
                //newLink.getHost().getBytes();
            } catch (Exception e) {
                continue;
            }
            if(newLink.getHost() != null) {
                if (newLink.getHost().equals(this.Domain) && !newLink.getPath().isEmpty()) {
                    if(newLink.getPath().contains("en.wikipedia")){

                    }
                        //
                    this.intraDomainLinks.add(newLink.getPath());
                } else if (!newLink.getHost().equals(this.Domain)){
                    this.interDomainLinks.add(found);
                }
            }
        }
    }

    private void addTitle(String content){
        Pattern titleGet = Pattern.compile("<title>(.*)</title>");
        Matcher title = titleGet.matcher(content);
        if(title.find()) {
            this.title = title.group(1);
        } else {
            this.title = "(DEBUG) TITLE UNKNOWN (DEBUG)";
        }
    }

    public List<String> getIntraDomainLinks(){
        return new ArrayList<>(intraDomainLinks);
    }

    public List<String> getInterDomainLinks(){
        return new ArrayList<>(interDomainLinks);
    }

    public void addLikedToByPage(String URL){
        this.linkedToByOtherPages.put(URL,1);
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("URL",this.URL.toString());
        jsonObject.put("title",this.title);
        jsonObject.put("wordUsage",this.wordUsage.toString());
        jsonObject.put("linkedToByOtherPages", this.linkedToByOtherPages.toString());
        jsonObject.put("updated", this.updated);
        jsonObject.put("score", this.score);
        return jsonObject;
    }

}
