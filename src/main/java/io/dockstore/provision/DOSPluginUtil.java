package io.dockstore.provision;

import com.google.common.collect.Lists;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_OK;


class DOSPluginUtil {

    private static final String API = "/ga4gh/dos/v1/dataobjects/";
    private static final int LIST_SIZE = 3;
    private static final int HOST = 1;
    private static final int UID = 2;

    // Package-private constructor
    DOSPluginUtil() {
    }

    /**
     * Gets the plugins json file path from the config file, otherwise defaults.
     *
     * @param dosURI The string targetPath
     * @return The targetPath split into an ArrayList object. Return an empty ArrayList object otherwise
     */
    List<String> splitUri(String dosURI) {
        if (Pattern.compile(":\\/\\/(.+)/").matcher(dosURI).find()){
            return Lists.newArrayList(dosURI.split(":\\/\\/|/"));
        }
        return Collections.emptyList();
    }

    /**
     * Gets the json response from host using HTTP GET method
     *
     * @param uriList The targetPath split into an ArrayList object with the following format: [scheme, host, uid]
     * @return The JSONObject containing the content of the json response. Null, otherwise
     */
    JSONObject grabJSON(List<String> uriList){
        String content;
        HttpURLConnection conn = null;

        if (uriList.size() != LIST_SIZE) { return null; }
        try {
            conn = createConnection("http", uriList);
            if (Objects.requireNonNull(conn).getResponseCode() != HTTP_OK) {
                try {
                    conn = createConnection("https", uriList);
                    if (Objects.requireNonNull(conn).getResponseCode() != HTTP_OK) { return null; }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            content = readResponse(conn.getInputStream());
        } catch (Exception e) {
            System.err.println("Plugin HttpURLConnection error: "  + e.getCause());
            e.printStackTrace();
            return null;
        } finally {
            assert conn != null;
            conn.disconnect();
        }
        return new JSONObject(content);
    }

    HttpURLConnection createConnection(String protocol, List<String> uriList) {
        try {
            URL request = new URL(protocol + "://" + uriList.get(HOST) + API +  uriList.get(UID));
            HttpURLConnection con = (HttpURLConnection) request.openConnection();
            return con;
        } catch ( IOException e) {
            System.err.println("ERROR opening HTTP URL Connection.");
            e.printStackTrace();
        }
        return null;
    }

    String readResponse(InputStream stream) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } catch (IOException e) {
            System.err.println("ERROR reading HTTP Response object.");
            e.printStackTrace();
        }
        return null;
    }
}
