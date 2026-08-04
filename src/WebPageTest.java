import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class WebPageTest {
    String User_Agent = "bot-bromide-crawler/1.0 (contact_bromide_crawler@proton.me) bromide-crawler/1.0";

    @Test
    public void testCreation() throws URISyntaxException, IOException, InterruptedException {
        WebPage test1 = new WebPage("https://en.wikipedia.org/wiki/Zebra", User_Agent);
        assert(test1.parse());
        assert(test1.export());
        String filename = "/media/disk1/crawlerOut/" + "en.wikipedia.org/wiki/Zebra" + ".json";
        File f = new File(filename);
        WebPage test2 = null;
        assert(f.exists());
        assert(!f.isDirectory());
        assert(f.canRead());
        FileReader myReader = new FileReader(f);
        JSONObject old = new JSONObject(myReader.readAllAsString());
        test2 = new WebPage(old, User_Agent);
        assert(test1.equals(test2));
    }
}
