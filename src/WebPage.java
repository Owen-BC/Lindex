import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPage {
    private String URL;
    private String Domain;
    private String user_agent;
    private String title;
    private Hashtable<String,Integer> WordUsage;
    private Hashtable<String, Integer> LinkedToByOtherPages;
    private List<String> intraDomainLinks;
    private List<String> interDomainLinks;
    private int score;
    private Date updated;


    public WebPage(String URL, String user_agent) throws URISyntaxException, IOException, InterruptedException {
        this.URL = URL;
        this.user_agent = user_agent;
        this.Domain = new URI(URL).getHost();
        this.WordUsage = new Hashtable<>();
        this.interDomainLinks = new ArrayList<>();
        this.intraDomainLinks = new ArrayList<>();
        parsePageData();
        Thread.sleep(1000);
    }

    private void parsePageData() throws URISyntaxException, IOException {
        BufferedReader in;

        URL url = new URI(URL).toURL();
        URLConnection connection = url.openConnection();
        connection.setRequestProperty(
                "User-Agent",
                user_agent
        );
        in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));

        if(in != null) {
            try {
                String curline;
                Add_Title(connection);
                boolean passedHead = false;
                while ((curline = in.readLine()) != null) {
                    if(curline.contains("</head>"))
                        passedHead = true;
                    if(passedHead) {
                        parseWordUsage(curline);
                        parseDomainLinks(curline);
                    }
                }
            } catch (IOException e) {
                // ignore for now
            }
        }
    }

    private void parseWordUsage(String parseLine) {
        Pattern removeHTMLTags = Pattern.compile("<[^<>]*>| ");
        String[] words = removeHTMLTags.split(parseLine);
        if(words == null)
            return;
        for(String word: words) {
            Integer result = WordUsage.get(word);
            if (result == null){
                result = 1;
            }
            WordUsage.put(word,result);
        }
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
                if (newLink.getHost().equals(this.Domain)) {
                    this.intraDomainLinks.add(found);
                } else {
                    this.interDomainLinks.add(found);
                }
            }
        }
    }

    private void Add_Title(URLConnection connection){
        this.title = connection.getHeaderField("Title");
    }

    public List<String> getIntraDomainLinks(){
        return new ArrayList<>(intraDomainLinks);
    }

    public List<String> getInterDomainLinks(){
        return new ArrayList<>(interDomainLinks);
    }

    public void AddLikedToByPage(String URL){
        LinkedToByOtherPages.put(URL,1);
    }

}
