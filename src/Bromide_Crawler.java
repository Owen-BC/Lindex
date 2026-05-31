// Takes as input entry points and crawls the internet, respecting robots.txt and only sending 1 request per second per domain.
// Copyright (C) 2026  Owen Butcher

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Bromide_Crawler {

    private Domain domain;
    private List<WebPage> pages;
    private List<String> intraDomainURL;
    private String user_agent = "bot-bromide-crawler/1.0 (contact_bromide_crawler@proton.me) bromide-crawler/1.0";

    public Bromide_Crawler(String EntryURL) {
        try {
            URI uri = new URI(EntryURL);
            domain = new Domain(uri, user_agent);
        } catch (Exception e) {
            return;
        }
    }

    public void run(List<String> EntryURLs) throws URISyntaxException, IOException, InterruptedException {
        List<String> intraDomainURLs = new ArrayList<String>();
        int count = 0;
        for(String url: EntryURLs){
            if(domain.check_URL(url)){
                // Domain is valid to crawl
                WebPage webpage = new WebPage(url,user_agent);
                intraDomainURLs.addAll(webpage.getIntraDomainLinks());
            }
        }

        for(String url: intraDomainURLs) {
            if(domain.check_URL(url)){
                // Domain is valid to crawl
                WebPage webpage = new WebPage(url,user_agent);
                intraDomainURLs.addAll(webpage.getIntraDomainLinks());
            }
        }
    }


}
