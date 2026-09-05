import org.json.JSONObject;

import static java.lang.System.exit;

/***
 * @param arguments
 *  --Entry-URLs "Path to file" | see Entry-URLs.md for instructions
 *  --help
 */
void main(String[] arguments) throws IOException {
    JSONObject settings = initSettings();
    String urlFile = parseArguments(arguments);

    String urls = fileUtil.readFile(new File(urlFile));
    assert urls != null;
    List<String> urlList = urls.lines().toList();

    IO.println(String.format("User_Agent: " + settings.get("User-Agent")));

    Hashtable<String, List<String>> inputList = WebUtil.getDistinctEntryLinkLists(urlList);
    for (String domain : inputList.keySet()) {
        Thread crawler = new Thread(new Bromide_Domain_Crawler(inputList.get(domain), (String) settings.get("User-Agent"), (String) settings.get("Output-Folder")));
        crawler.start();
    }
}

/** Gets the settings for this instance of the bromide crawler system, if no settings file is found, creates a default
 *  settings file, if the file is invalid, throws.
 *
 * @return JSONObject containing:
 *  User-Agent - user agent string sent to websites
 *  outputFolder - location where the output data of this crawler will be stored
 */
public JSONObject initSettings() throws IOException {
    JSONObject settings = fileUtil.getSettings();
    assert (settings.get("User-Agent") != null);
    assert (settings.get("Output-Folder") != null);
    assert (!settings.get("User-Agent").toString().contains("USER-CONTACT-ADDRESS-HERE"));
    return settings;
}

/** Creates the default settings for a instance of the bromideCrawler system
 *
 * @return default settings json object
 */
public JSONObject createDefaultSettings() throws IOException {
    JSONObject settings = new JSONObject();
    settings.put("User-Agent", "bot-bromide-crawler/1.0 (USER-CONTACT-ADDRESS-HERE)");
    String path = new File(".").getCanonicalPath() + "/crawlerOut";
    settings.put("Output-Folder", path);
    return settings;
}

public String parseArguments(String[] arguments) {
    boolean nextEntryURLS = false;
    for (String arg : arguments) {
        if (arg.equals("--help")) {
            IO.println("Bromide Daemon V0.1");
            IO.println("Arguments:");
            IO.println("--Entry-URLs \"Path to file\" - list of files to parse");
            IO.println("--help - prints this message and exits");
            exit(0);
        } else if (arg.equals("--Entry-URLs")) {
            nextEntryURLS = true;
        } else if (nextEntryURLS) {
            return arg;
        } else {
            throw new RuntimeException("Invalid argument passed, --help for info");
        }
    }
    return null;
}
