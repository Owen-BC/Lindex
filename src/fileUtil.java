import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Hashtable;

public class fileUtil {
    public static boolean exportWebpageData(Path directory){

        return false;
    }

    public static boolean importWebpageData(Path directory){

        return false;
    }



    private static Hashtable<Integer, Integer> encodeHashTable(Hashtable<String, Integer> data, Hashtable<String, Integer> dict){

        return null;
    }

    private static Hashtable<String, Integer> decodeHashTable(Hashtable<Integer, Integer> data, Hashtable<Integer, String> dict){

        return null;
    }

    private static Hashtable<String, Integer> getDictionary(){

        return null;
    }

    private static Hashtable<Integer, String> getDecodeDictionary(Hashtable<String, Integer> dict){
        Hashtable<Integer, String> myNewHashTable = new Hashtable<>();
        for(java.util.Map.Entry<String, Integer> entry : dict.entrySet()){
            myNewHashTable.put(entry.getValue(), entry.getKey());
        }
        return myNewHashTable;
    }

    private static boolean updateDictionary(){

        return false;
    }

    public static JSONObject getSettings() throws IOException {
        String settingsPath = System.getProperty("user.dir") + "/src/settings.json";
        String output = readFile(new File(settingsPath));
        assert output != null;
        return new JSONObject(output);
    }

    public static boolean updateSettings(JSONObject settings) throws IOException {
        String settingsPath = System.getProperty("user.dir") + "/src/settings.json";
        return writeFile(settingsPath, settings);
    }

    /***
     * writes a file
     * @param filePath
     * @param data
     * @return
     */
    public static boolean writeFile(String filePath, Object data){
        try {
            File f = new File(filePath);
            if (f.getParentFile().mkdirs() || f.getParentFile().exists()) {   // Creates all missing directories
                FileWriter myWriter = new FileWriter(filePath);
                myWriter.write(data.toString());
                myWriter.close();
                return true;
            } else {
                IO.println("Failed to create needed files for " + f.getParentFile());
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static String readFile(File f){
        try {
            if (f.getParentFile().mkdirs() || f.getParentFile().exists()) {   // Creates all missing directories
                FileReader myReader = new FileReader(f);
                String allData = myReader.readAllAsString();
                myReader.close();
                return allData;
            } else {
                IO.println("Failed to create needed files for " + f.getParentFile());
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
