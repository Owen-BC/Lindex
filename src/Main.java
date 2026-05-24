//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() throws IOException {
  //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
  // to see how IntelliJ IDEA suggests fixing it.
  IO.println(String.format("Hello and welcome!"));

  Domain_Index wiki = new Domain_Index("https://en.wikipedia.org");
  IO.println(Boolean.toString(wiki.check_URL("https://en.wikipedia.org/wiki/World_War_I")));
  IO.println(Boolean.toString(wiki.check_URL("https://en.wikipedia.org/wiki/Wikipedia:Changing_username")));
}
