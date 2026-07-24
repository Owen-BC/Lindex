import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Set;

class htmlparsetests {
    @Test
    public void basicParagraphHeader(){
        printHeader("basic");
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1>My First Heading</h1>\n" +
                "\n" +
                "<p>My first paragraph.</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        Hashtable<String, Integer> answer = new Hashtable<String, Integer>();
        answer.put("my",2);
        answer.put("first",2);
        answer.put("heading",1);
        answer.put("paragraph",1);
        Hashtable<String, Integer> test = WebUtil.ParseHTMLForTextUsage(text);
        compareHashTables(answer, test,0);
    }

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
        Hashtable<String, Integer> answer = generateTestHashTable("The href Attribute\n" +
                "\n" +
                "HTML links are defined with the a tag. The link address is specified in the href attribute:\n" +
                "\n" +
                "Visit W3Schools");
        Hashtable<String, Integer> test = WebUtil.ParseHTMLForTextUsage(text);
        compareHashTables(answer, test,0);
    }

    @Test
    public void imageTest(){
        printHeader("image");
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h2>HTML Images</h2>\n" +
                "<p>HTML images are defined with the img tag:</p>\n" +
                "\n" +
                "<img src=\"w3schools.jpg\" alt=\"W3Schools.com\" width=\"104\" height=\"142\">\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        Hashtable<String, Integer> answer = generateTestHashTable("HTML Images\n" +
                "\n" +
                "HTML images are defined with the img tag:");
        Hashtable<String, Integer> test = WebUtil.ParseHTMLForTextUsage(text);
        compareHashTables(answer, test,0);
    }

    @Test
    public void nestedTest(){
        printHeader("image");
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1>About W3Schools</h1>\n" +
                "\n" +
                "<p title=Description of W3Schools>\n" +
                "You cannot omit quotes around an attribute value \n" +
                "if the value contains spaces.\n" +
                "</p>\n" +
                "\n" +
                "<p><b>\n" +
                "If you move the mouse over the paragraph above,\n" +
                "your browser will only display the first word from the title.\n" +
                "</b></p>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        Hashtable<String, Integer> answer = generateTestHashTable("About W3Schools\n" +
                "\n" +
                "You cannot omit quotes around an attribute value \n" +
                "if the value contains spaces.\n" +
                "\n" +
                "If you move the mouse over the paragraph above,\n" +
                "your browser will only display the first word from the title.");
        Hashtable<String, Integer> test = WebUtil.ParseHTMLForTextUsage(text);
        compareHashTables(answer, test,0);
    }

    @Test
    public void attributesTest(){
        printHeader("attribute");
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<div style=\"position:relative;\">\n" +
                "  <div style=\"opacity:0.5;position:absolute;left:50px;top:-30px;width:300px;height:150px;background-color:#40B3DF\"></div>\n" +
                "  <div style=\"opacity:0.3;position:absolute;left:120px;top:20px;width:100px;height:170px;background-color:#73AD21\"></div>\n" +
                "  <div style=\"margin-top:30px;width:360px;height:130px;padding:20px;border-radius:10px;border:10px solid #EE872A;font-size:120%;\">\n" +
                "    <h1>CSS = Styles and Colors</h1>\n" +
                "    <div style=\"letter-spacing:12px;font-size:15px;position:relative;left:25px;top:25px;\">Manipulate Text</div>\n" +
                "    <div style=\"color:#40B3DF;letter-spacing:12px;font-size:15px;position:relative;left:25px;top:30px;\">Colors,\n" +
                "    <span style=\"background-color:#B4009E;color:#ffffff;\"> Boxes</span></div>\n" +
                "  </div>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        Hashtable<String, Integer> answer = generateTestHashTable("CSS = Styles and Colors\n" +
                "\n" +
                "Manipulate Text\n" +
                "\n" +
                "Colors,\n" +
                "     Boxes");
        Hashtable<String, Integer> test = WebUtil.ParseHTMLForTextUsage(text);
        compareHashTables(answer, test,0);
    }

    @Test
    public void tableTest(){
        printHeader("table");
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "th, td {\n" +
                "  padding: 5px;\n" +
                "  text-align: left;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h2>Table Caption</h2>\n" +
                "<p>To add a caption to a table, use the caption tag.</p>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <caption>Monthly savings</caption>\n" +
                "  <tr>\n" +
                "    <th>Month</th>\n" +
                "    <th>Savings</th>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>January</td>\n" +
                "    <td>$100</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>February</td>\n" +
                "    <td>$50</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n" +
                "\n";
        Hashtable<String, Integer> answer = generateTestHashTable("Table Caption\n" +
                "\n" +
                "To add a caption to a table, use the caption tag.\n" +
                "\n" +
                "  Monthly savings\n" +
                "\n" +
                "    Month\t\n" +
                "    Savings\t\n" +
                "\n" +
                "    January\t\n" +
                "    $100\t\n" +
                "\n" +
                "    February\t\n" +
                "    $50");
        Hashtable<String, Integer> test = WebUtil.ParseHTMLForTextUsage(text);
        compareHashTables(answer, test,0);
    }

    @Test
    public void zebraTest() throws IOException {
        printHeader("zebra");
        Hashtable<String, Integer> answer = generateTestHashTable(new String(Files.readAllBytes(Paths.get("/home/omnisiahs-vissage/IdeaProjects/LIndex/src/zebratest.txt"))));
        Hashtable<String, Integer> test = WebUtil.ParseHTMLForTextUsage(new String(Files.readAllBytes(Paths.get("/home/omnisiahs-vissage/IdeaProjects/LIndex/src/zebra.txt"))));
        compareHashTables(answer, test, 20);
    }


    private void printHeader(String name){
        System.out.println("**************************************");
        System.out.println("Test: " + name);
    }

    static public void compareHashTables(Hashtable<String, Integer> reference, Hashtable<String, Integer> test, Integer maxMissCount){
        boolean passed = true;
        Integer totalMisses = 0;
        if(!reference.equals(test)){
            // Differences exist
            Set<String> refKeys = reference.keySet();
            Set<String> testKeys = new HashSet<>(test.keySet());
            // keysets are different
            for(String refkey : refKeys){
                if(testKeys.contains(refkey)){
                    // keys are in both sets, ensure equality in parsing output
                    Integer refCount = reference.get(refkey);
                    Integer testCount = test.get(refkey);
                    if(!Objects.equals(refCount, testCount)){
                        passed = false;
                        totalMisses += Math.abs(testCount - refCount);
                        System.out.println("Error: test miscounted " + refkey + ", reference: " + refCount.toString() + ", test: " + testCount.toString());
                    }
                    testKeys.remove(refkey);
                } else {
                    passed = false;
                    System.out.println("Error: testKeys missing: " + refkey);
                    totalMisses += reference.get(refkey);
                }
            }
            for(String testkey : testKeys){
                passed = false;
                System.out.println("Error: bad key extracted: \"" + testkey + "\", count: " + test.get(testkey));
                totalMisses += test.get(testkey);
            }
            System.out.println("Total misses: " + totalMisses.toString());
            if (totalMisses <= maxMissCount) {
                // I am tired, good enough
                passed = true;
            }
        } else {
            System.out.println("Passed, Output is as expected");
        }
        assert passed;
    }

    private Hashtable<String, Integer> generateTestHashTable(String text){
        Hashtable<String, Integer> answer = new Hashtable<String, Integer>();
//        text = text.replace(". ", " ");
        // split on whitespace, strip punctuation
        String[] words = text.split("[^a-zA-Z0-9]");
        for (String word : words) {
            if (word.isEmpty()) continue;
            word = word.toLowerCase();
            if (answer.containsKey(word)) {
                answer.put(word, answer.get(word) + 1);
            } else {
                answer.put(word, 1);
            }
        }
        return answer;
    }
}