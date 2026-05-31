// Represents a given domain name on the internet. Used to determine if a domain is indexable
// Copyright (C) 2026  Owen Butcher


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Domain {
    private final String domainProtocol;
    private final String domain_name;
    private final List<Pattern> allowed_robots_txt;
    private final List<Pattern> disallowed_robots_txt;
    private Boolean has_sitemap;
    public final String User_Agent;
    private String sitemap_path;


    public Domain(java.net.URI domain_name, String User_Agent) throws IOException, InterruptedException {
        this.domain_name = domain_name.getHost();
        this.domainProtocol = domain_name.getScheme();
        this.allowed_robots_txt = new ArrayList<>();
        this.disallowed_robots_txt = new ArrayList<>();
        this.has_sitemap = false;
        this.User_Agent = User_Agent;

        parse_robots_txt();
        if(this.has_sitemap)
            parse_sitemap();
        Thread.sleep(1000);
    }

    private void parse_robots_txt() throws IOException {
        String robots_txt_url = this.domainProtocol.concat("://".concat(this.domain_name.concat("/robots.txt")));
        BufferedReader in;
        in = WebUtil.Create_Buffer_Reader_From_URL(robots_txt_url,User_Agent);
        if (in == null) {

            allowed_robots_txt.add(Pattern.compile(".*"));
            return;
        }
        String inputLine;
        Pattern allow_patter = Pattern.compile("^Allow: *",Pattern.CASE_INSENSITIVE);
        Pattern disallow_pattern = Pattern.compile("^Disallow: *",Pattern.CASE_INSENSITIVE);
        Pattern user_agent_pattern = Pattern.compile("^User-agent: *",Pattern.CASE_INSENSITIVE);
        Pattern sitemap_pattern = Pattern.compile("^Sitemap: *",Pattern.CASE_INSENSITIVE);

        boolean found_user_agent = false;
        boolean command_since_user_agent = false; // we can stop listening after we see a user agent after out command
        while ((inputLine = in.readLine()) != null) {
            // Strip of comments
            int k = inputLine.indexOf("#");
            if(k != -1) {
                inputLine = inputLine.substring(0, k);
            }

            // Check if the line is a user agent line
            Matcher matcher = user_agent_pattern.matcher(inputLine);
            if(matcher.find()) {
                if(!found_user_agent) {
                    String value = inputLine.substring(matcher.end()).strip();
                    if(value.equals("*") || User_Agent.contains(value)) {
                        found_user_agent = true;
                    }
                    continue;
                } else if(command_since_user_agent) {
                    break;
                }
            }

            // Check if the line indicates a sitemap
            if(!this.has_sitemap) {
                matcher = sitemap_pattern.matcher(inputLine);
                if (matcher.find()) {
                    this.has_sitemap = true;
                    this.sitemap_path = inputLine.substring(matcher.end()).strip();
                    continue;
                }
            }

            // We have found our user agent, record the results
            if(found_user_agent) {

                // Check for allow lines
                matcher = allow_patter.matcher(inputLine);
                if (matcher.find()) {
                    command_since_user_agent = true;
                    String toappend = inputLine.substring(matcher.end()).strip(); // first char after Allow:
                    if (!toappend.isEmpty()) {
                        Pattern pattern = Pattern.compile(toappend);
                        this.allowed_robots_txt.add(pattern);
                    }
                    continue;
                }

                // Check for disallow lines
                matcher = disallow_pattern.matcher(inputLine);
                if (matcher.find()) {
                    command_since_user_agent = true;
                    String toappend = inputLine.substring(matcher.end()).strip(); // first char after Allow:
                    if (!toappend.isEmpty()) {
                        Pattern pattern = Pattern.compile(toappend);
                        this.disallowed_robots_txt.add(pattern);
                    }
                }
            }
        }
    }

    private void parse_sitemap() {
        return; // not currently implimented
    }


    // Stupid brute force implementation, use a tree for better performance
    public boolean check_URL(String URL){
        // Let us make sure this is even the right domain
        assert (URL.startsWith(domain_name));

        int current_longest_disallow = 0;
        int current_longest_allow = 0;
        // First we check if any of the disallow statements apply or are more specific then the current allow statement
        for(Pattern disallow_rule: disallowed_robots_txt){
            Matcher matcher = disallow_rule.matcher(URL);
            if(matcher.find()) {
                if(matcher.end() - matcher.start() > current_longest_disallow)
                    current_longest_disallow = matcher.end() - matcher.start();
            }
        }

        if(current_longest_disallow != 0) {
            for (Pattern allow_rule : allowed_robots_txt) {
                Matcher matcher = allow_rule.matcher(URL);
                if (matcher.find()) {
                    if(matcher.end() - matcher.start() > current_longest_allow)
                        current_longest_allow = matcher.end() - matcher.start();
                }
            }
        }

        // Check what the most specific allow clause is, choosing to allow if there is a tie
        return current_longest_allow >= current_longest_disallow;
    }

    public WebPage create_webpage(String URL){
        WebUtil.Create_Buffer_Reader_From_URL(URL,this.User_Agent);

        return null;
    }

}
