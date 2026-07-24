import org.junit.jupiter.api.Test;

//import org.jsoup.Jsoup;
//import org.jsoup.helper.Validate;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
import java.util.Hashtable;

public class htmllinkparsetests {
    @Test
    public void hrefTest(){
        printHeader("href");
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h2>The href Attribute</h2>\n" +
                "\n" +
                "<p>HTML links are defined with the a tag. The link address is specified in the href attribute:</p>\n" +
                "\n" +
                "<a href=\"https://www.w3schools.com\">Visit W3Schools</a>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n" +
                "\n" +
                "\n";
//        Document doc = Jsoup.connect(url).get();
//        Elements links = doc.select("a[href]");
//        Hashtable<String, Integer> test = WebUtil.ParseHTMLForTextUsage(text);
//        compareHashTables(answer, test,0);
    }

    private void printHeader(String name){
        System.out.println("**************************************");
        System.out.println("Test: " + name);
    }
}
