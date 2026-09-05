// Takes as input entry points and crawls the internet, respecting robots.txt and only sending 1 request per second per domain.
// Copyright (C) 2026  Owen Butcher

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.io.FileWriter;

public class Bromide_Domain_Crawler implements Runnable {

    private Domain domain;
    private List<WebPage> pages;
    private List<String> entryURLs;
    private final HashSet<String> intraDomainURLs;
    private final Hashtable<String, Boolean> checkedTables;
    private HashSet<String> urlsToAdd;
    private Hashtable<String, Integer> fileEncodeDict;
    private String user_agent;
    private Date start;
    private String outFolder;

    public Bromide_Domain_Crawler(List<String> EntryURLs, String userAgent, String outFolder) {
        this.intraDomainURLs = new HashSet<String>();
        this.checkedTables = new Hashtable<String, Boolean>();
        this.urlsToAdd = new HashSet<>();
        this.user_agent = userAgent;
        this.start = new Date();
        this.outFolder = outFolder;
        try {
            URI uri = new URI(EntryURLs.getFirst());
            this.domain = new Domain(uri, user_agent, outFolder);
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
        intraDomainURLs.addAll(this.entryURLs);
        do{
            this.urlsToAdd = new HashSet<>();
            long lastCall = new Date().getTime();
            for(String url: intraDomainURLs) {
                if(checkedTables.get(url) == null) {
                    try {
                        if (processURL(domain.getBaseName() + url)) {
                            Date now = new Date();
                            long cur = now.getTime();
                            Thread.sleep(Math.max(6000 - cur + lastCall, 0));
                            count++;
                            lastCall = cur;
                            if(count % 10 == 0){
                                Long uptime = (Long)((now.getTime() - start.getTime()) / 1000);
                                IO.println("******************************");
                                IO.println("domain:" + domain.getDomainName());
                                IO.println("STATUS REPORT");
                                IO.println("Uptime " + (uptime.toString()));
                                IO.println("URL's Processed " + ((Integer)count).toString());
                                IO.println("URL's in queue:" + urlsToAdd.size());
                                IO.println("Number of minutes till current queue is cleared: " + ((Integer)(urlsToAdd.size() / 60)).toString());
                                IO.println("******************************");
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
        boolean returnValue = false;
        try {
            if (domain.check_URL(url)) {
//                IO.println(url + " " + new Date());
                // Domain is valid to crawl
                String filename = outFolder + "domain/" + domain.getDomainName() + new URI(url).getPath() + ".json";
                File f = new File(filename);
                WebPage webpage = null;
                boolean fresh = false;
                if(f.exists() && !f.isDirectory() && f.canRead()) {
                    FileReader myReader = new FileReader(f);
                    JSONObject old = new JSONObject(myReader.readAllAsString());
                    webpage = new WebPage(old, user_agent, outFolder);
                    fresh = webpage.fresh();
                } else {
                    webpage = new WebPage(url, user_agent, outFolder);
                }
                boolean parsed = false;
                if(!fresh) {
                    parsed = webpage.parse(); // parses webpage and creates record of results
                    returnValue = true;
                }
                List<String> links = webpage.getIntraDomainLinks();
                for(String s: links){
                    if(checkedTables.get(s) == null) {
                        if (domain.check_URL(s)) {
                            urlsToAdd.add(s);
                        }
                    }
                }
                if(parsed){
                    webpage.export();
                }
            }
        } catch(IOException | URISyntaxException | InterruptedException e){
            IO.println(url + " Failed to parse, " + e);
        }
        return returnValue;
    }

    private void loadDomainsParsedItems(){
        String directoryPath = outFolder + "domain/" + domain.getDomainName() + "/";

        // Using File class create an object for specific directory
        File directory = new File(directoryPath);

        // Using listFiles method we get all the files of a directory
        // return type of listFiles is array
        File[] files = directory.listFiles();

        // Print name of the all files present in that path
        if (files != null) {
            for (File file : files) {
//                System.out.println(file.getName());
                String pageName = file.getName();
                pageName = pageName.replace(".json", "");
                checkedTables.put(pageName,true);
            }
        }
    }
}
