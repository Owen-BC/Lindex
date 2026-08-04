import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPage {
    private URI URL;
    private String Domain;
    private String user_agent;
    private String title;
    private Hashtable<String,Integer> wordUsage;
    private Set<String> intraDomainLinks;
    private Set<String> interDomainLinks;
    private int score;
    private String updated;
    private int threshHold = 3;


    public WebPage(String URL, String user_agent) throws URISyntaxException, IOException, InterruptedException {
        this.URL = new URI(URL);
        this.user_agent = user_agent;
        this.Domain = this.URL.getHost();
        this.title = "NAN";
        this.wordUsage = new Hashtable<>();
        this.interDomainLinks = new HashSet<>();
        this.intraDomainLinks = new HashSet<>();
        this.updated = null;
        this.score = 0;
//        Thread.sleep(1000); // this is a stupid way to enforce one request per second dweebo
    }

    public boolean parse(){
        try {
            this.parsePageData() ;
        } catch (ExportException e) {
            return true; // returned when we get no data from the connection but no error, likely was not updated since last crawl
        }
        catch (URISyntaxException | IOException e) {
            IO.println("Error for WebPage Object " + URL.toString() + ", " + e);
            return false;
        }
        return true;
    }


    public WebPage(JSONObject data, String user_agent) throws URISyntaxException, IOException, InterruptedException {
        this.URL = new URI((String) data.get("URL"));
        this.user_agent = user_agent;
        this.title = data.get("title").toString();
        this.Domain = data.get("Domain").toString();
        this.updated = data.get("updated").toString();
        this.score = (int) data.get("score");

        wordUsage = new Hashtable<>();
        JSONObject arr1 = data.getJSONObject("wordUsage");
        for (Iterator<String> it = arr1.keys(); it.hasNext(); ) {
            String s = it.next();
            wordUsage.put(s, (Integer) arr1.get(s));
        }

        interDomainLinks = new HashSet<>();
        JSONArray arr2 = data.getJSONArray("interDomainLinks");
        for (int i = 0; i < arr2.length(); i++) {
            interDomainLinks.add(arr2.getString(i));
        }

        intraDomainLinks = new HashSet<>();
        JSONArray arr3 = data.getJSONArray("intraDomainLinks");
        for (int i = 0; i < arr3.length(); i++) {
            intraDomainLinks.add(arr3.getString(i));
        }
    }


    private void parsePageData() throws URISyntaxException, IOException {
        BufferedReader in;

        URL url = URL.toURL();
        URLConnection connection = url.openConnection();
        connection.setRequestProperty(
                "User-Agent",
                user_agent
        );

        // Used from https://stackoverflow.com/questions/21682448/java-time-in-milliseconds-to-http-format
        if(updated != null) {
            connection.setRequestProperty(
                    "If-Modified-Since",
                    updated
            );
        } else {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            updated = sdf.format(new Date());
        }

        in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        StringBuilder fullText = new StringBuilder();
        String curline;
        while ((curline = in.readLine()) != null) {
            fullText.append(curline);
        }
        if (fullText.toString().isEmpty()){
            throw new ExportException(((HttpURLConnection)connection).getResponseMessage());
        } else {
            String fulltextString = fullText.toString();
            addTitle(fulltextString);
            wordUsage = WebUtil.ParseHTMLForTextUsage(fulltextString);
            parseDomainLinks(fulltextString);
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
                if (newLink.getHost().equals(this.Domain) && !newLink.getPath().isEmpty()) {
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

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("URL",this.URL);
        jsonObject.put("title",this.title);
        jsonObject.put("Domain", this.Domain);
        jsonObject.put("wordUsage",this.wordUsage);
        jsonObject.put("interDomainLinks",this.interDomainLinks);
        jsonObject.put("intraDomainLinks",this.intraDomainLinks);
        jsonObject.put("updated", this.updated);
        jsonObject.put("score", this.score);
        return jsonObject;
    }

    public boolean export() throws IOException {
        if(Domain.isEmpty() || updated == null || (wordUsage.isEmpty() && intraDomainLinks.isEmpty() && interDomainLinks.isEmpty()))
            return false; // if we dont have valid information, don't write
        String filename = "/media/disk1/crawlerOut/" + Domain + URL.getPath() + ".json";
        File f = new File(filename);
        if(f.getParentFile().mkdirs() || f.getParentFile().exists()) {   // Creates all missing directories
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(this.toJson().toString());
            myWriter.close();

            return true;
        } else {
            IO.println("Failed to create needed files for " + f.getParentFile());
        }
        return false;
    }

    // checks if the link has been checked in the last day, returns true if this is the case and false otherwise
    public boolean fresh(){
        Date updatedTime = new Date(this.updated);
        Date curTime = new Date();
        int millsInADay = 43200000;
        return (curTime.getTime() - updatedTime.getTime()) < (millsInADay * 30);
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode() && obj.getClass() == this.getClass();
    }


    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(URL)
                .append(Domain)
                .append(user_agent)
                .append(title)
                .append(wordUsage)
                .append(intraDomainLinks)
                .append(interDomainLinks)
                .append(score)
                .append(updated)
                .append(threshHold).hashCode();
    }
}
