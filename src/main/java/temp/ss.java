package temp;

import java.util.HashMap;

public class ss {

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("1", "1");
        map.put("2", "1");
        map.put("3", "1");
        for (String key : map.keySet()) {
            map.remove("1");
        }

    }

}
