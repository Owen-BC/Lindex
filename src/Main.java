// Main function currently in use for testing
// Copyright (C) 2026  Owen Butcher

void main() throws IOException {
  IO.println(String.format("Hello and welcome!"));

  Domain_Index wiki = new Domain_Index("https://en.wikipedia.org");
  IO.println(Boolean.toString(wiki.check_URL("https://en.wikipedia.org/wiki/World_War_I")));
  IO.println(Boolean.toString(wiki.check_URL("https://en.wikipedia.org/wiki/Wikipedia:Changing_username")));
}
