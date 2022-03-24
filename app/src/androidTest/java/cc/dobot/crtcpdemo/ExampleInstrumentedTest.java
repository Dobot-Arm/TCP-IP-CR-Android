package cc.dobot.crtcpdemo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("cc.dobot.crtcpdemo", appContext.getPackageName());
    }

    @Test
    public void subErrorIDTest() throws JSONException {
        String errorID="0,{[[-2],[],[],[],[],[]]},GetErrorID();";
        int startErrorIDIndex=errorID.indexOf(",");
        int endErrorIDIndex=errorID.indexOf(",GetErrorID();");
        System.out.println("do array test"+errorID.substring(startErrorIDIndex+2,endErrorIDIndex-1));
        JSONArray array=new JSONArray(errorID.substring(startErrorIDIndex+2,endErrorIDIndex-1));
        for (int i=0;i<array.length();i++){
            JSONArray array1=array.getJSONArray(i);
            System.out.println("array:"+array1.toString()+"   array size:"+array.length()+"  "+i);
        }
        assertEquals("{[[-2],[],[],[],[],[]]}",errorID.substring(startErrorIDIndex+1,endErrorIDIndex));

    }
}