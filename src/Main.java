// Main function currently in use for testing
// Copyright (C) 2026  Owen Butcher

void main() throws IOException, InterruptedException, URISyntaxException {
  IO.println(String.format("Hello and welcome!"));
  String User_Agent = "bot-bromide-crawler/1.0 (contact_bromide_crawler@proton.me) bromide-crawler/1.0";
//
//  Domain wiki = new Domain("https://en.wikipedia.org", User_Agent);
//  IO.println(Boolean.toString(wiki.check_URL("https://en.wikipedia.org/wiki/World_War_I")));
//  IO.println(Boolean.toString(wiki.check_URL("https://en.wikipedia.org/wiki/Wikipedia:Changing_username")));
  Bromide_Crawler crawler = new Bromide_Crawler("https://en.wikipedia.org/wiki/Zebra");
  List<String> input = new ArrayList<>();
  input.add("https://en.wikipedia.org/wiki/Zebra");
  crawler.run(input);
}
