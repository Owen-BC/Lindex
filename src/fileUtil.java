import java.nio.file.Path;
import java.util.HashMap;
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
}
