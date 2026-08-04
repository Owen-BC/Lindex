// Main function currently in use for testing
// Copyright (C) 2026  Owen Butcher

void main() {
  IO.println("Starting Bromide Crawler");
  String User_Agent = "bot-bromide-crawler/1.0 (contact_bromide_crawler@proton.me) bromide-crawler/1.0";
  IO.println(String.format("User_Agent: " + User_Agent));

  List<String> input = new ArrayList<>();
  input.add("https://en.wikipedia.org/wiki/Zebra");
  input.add("https://stackoverflow.com/questions");
  input.add("https://store.steampowered.com/");
  input.add("https://xkcd.com/");

  input.add("https://starwars.fandom.com/wiki/Main_Page");
//  input.add("https://starwars.fandom.com/wiki/Star_Wars:_Hyperspace_Stories:_The_Bad_Batch%E2%80%94Rogue_Agents");

  Hashtable<String, List<String>> inputLists = WebUtil.getDistinctEntryLinkLists(input);
  for(String domain: inputLists.keySet()) {
    Thread crawler = new Thread(new Bromide_Domain_Crawler(inputLists.get(domain), User_Agent));
    crawler.start();
  }
}
