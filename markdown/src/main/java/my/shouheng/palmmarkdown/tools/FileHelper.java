package my.shouheng.palmmarkdown.tools;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shouh on 2018/3/26.*/
public class FileHelper {

    public static String readAssetsContent(Context context, String fileName) {
        try {
            InputStream inStream = context.getResources().getAssets().open(fileName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, len);
            }
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
