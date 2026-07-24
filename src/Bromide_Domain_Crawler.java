// Takes as input entry points and crawls the internet, respecting robots.txt and only sending 1 request per second per domain.
// Copyright (C) 2026  Owen Butcher

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.io.FileWriter;

public class Bromide_Domain_Crawler implements Runnable {

    private Domain domain;
    private List<WebPage> pages;
    private List<String> entryURLs;
    private final List<String> intraDomainURLs;
    private final Hashtable<String, Boolean> checkedTables;
    private final ArrayList<String> urlsToAdd;
    private Hashtable<String, Integer> fileEncodeDict;
    private String user_agent;

    public Bromide_Domain_Crawler(List<String> EntryURLs, String userAgent) {
        this.intraDomainURLs = new ArrayList<String>();
        this.checkedTables = new Hashtable<String, Boolean>();
        this.urlsToAdd = new ArrayList<>();
        user_agent = userAgent;
        try {
            URI uri = new URI(EntryURLs.getFirst());
            this.domain = new Domain(uri, user_agent);
            entryURLs = EntryURLs;
        } catch (Exception e) {
            IO.println(String.format("Warning: " + e.toString()));
        }
        loadDomainsParsedItems();
    }

    @Override
    public void run() {
        int count = 0;
        for(String url: this.entryURLs){
            if(checkedTables.get(url) == null) {
                try {
                    if (processURL(url)) {
                        Thread.sleep(1000);
                        count++;
                    }
                } catch (InterruptedException e) {
                    IO.println(String.format("Warning: " + e.toString()));
                }
                intraDomainURLs.addAll(urlsToAdd);
            }
        }

        do{
           for(String url: intraDomainURLs) {
                if(checkedTables.get(url) == null) {
                    try {
                        if (processURL(domain.getBaseName() + url)) {
                            Thread.sleep(1000);
                            count++;
                            if (count > 6) {
                                return;
                            }
                        }
                    } catch (InterruptedException e) {
                        IO.println(String.format("Warning: " + e.toString()));
                    }
                }
           }
           intraDomainURLs.addAll(urlsToAdd);
        }while(!urlsToAdd.isEmpty());

    }

    private boolean processURL (String url) throws InterruptedException {
        try {
            if (domain.check_URL(url)) {
                IO.println(url + " " + new Date());
                // Domain is valid to crawl
                String filename = "/media/disk1/crawlerOut/" + domain.getDomainName() + "/" + new URI(url).getPath().replace("/", "-") + ".json";
                File f = new File(filename);
//                if(f.exists() && !f.isDirectory()) {
////                    FileReader myReader = new FileReader(f);
////                    JSONObject old = new JSONObject(myReader.readAllAsString());
////                    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
////                    Date updated = dateFormat.parse(old.get("updated").toString());
////                    URL url2 = new URI(url).toURL();
////                    URLConnection connection = url2.openConnection();
////                    connection.setRequestProperty(
////                            "User-Agent",
////                            user_agent
////                    );
////                    connection.setRequestProperty(
////                            "If-Modified-Since",
////                            String.valueOf(updated.getTime())
////                    );
//                    return false;
//                }
                WebPage webpage = new WebPage(url, user_agent);
                this.urlsToAdd.addAll(webpage.getIntraDomainLinks());
                f.getParentFile().mkdirs();   // Creates all missing directories
                FileWriter myWriter = new FileWriter(filename);
                myWriter.write(webpage.toJson().toString());
                myWriter.close();
                checkedTables.put(url, true);
                IO.println(new Date());
            } else {
                return false;
            }
        } catch(IOException | URISyntaxException | InterruptedException e){
            IO.println(url + " Failed to parse");
            Thread.sleep(1000);
            return false;
        }
        return true;
    }

    private void loadDomainsParsedItems(){
        String directoryPath ="/media/disk1/crawlerOut/" + domain.getDomainName() + "/";

        // Using File class create an object for specific directory
        File directory = new File(directoryPath);

        // Using listFiles method we get all the files of a directory
        // return type of listFiles is array
        File[] files = directory.listFiles();

        // Print name of the all files present in that path
        if (files != null) {
            for (File file : files) {
//                System.out.println(file.getName());
                String pageName = file.getName().replace("-","/");
                pageName = pageName.split("\\.json")[0];
                checkedTables.put(pageName,true);
            }
        }
    }
}
