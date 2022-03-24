package cc.dobot.crtcpdemo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void subErrorIDTest() throws JSONException {
        String errorID="0,{[[-2],[],[],[],[],[]]},GetErrorID();";
        int startErrorIDIndex=errorID.indexOf(",");
        int endErrorIDIndex=errorID.indexOf(",GetErrorID();");

        JSONArray array=new JSONArray(errorID.substring(startErrorIDIndex+2,endErrorIDIndex-1));
        for (int i=0;i<array.length();i++){
            JSONArray array1=array.getJSONArray(i);
            System.out.println(array1.toString());
        }
        assertEquals("{[[-2],[],[],[],[],[]]}",errorID.substring(startErrorIDIndex+1,endErrorIDIndex));

    }
}