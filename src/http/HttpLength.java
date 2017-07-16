package http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/10.
 */
public class HttpLength {

    HttpURLConnection conn;
    int length = 0;
    String name;
    String url;

    public HttpLength(String url) {
        this.url = url;

        try {
            URL url1 = new URL(url);
            conn = (HttpURLConnection) url1.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setConnectTimeout(1000 * 5);
            if (conn.getResponseCode() == 200) {
                length = conn.getContentLength();
                name = conn.getHeaderField("content-disposition");
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        if (name == null) {
            String[] strings = url.split("/");
            return strings[strings.length - 1];
        }
        String s = "filename=\"(.*)?\"";
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher(name);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return name;
    }
}
