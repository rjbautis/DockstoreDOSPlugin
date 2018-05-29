package io.dockstore.provision;

import com.google.common.collect.Lists;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DOSPluginUtil {

    /**
     * Gets the plugins json file path from the config file, otherwise defaults.
     *
     * @param dosURI The string targetPath
     * @return The targetPath split into an ArrayList object. Return an empty ArrayList object otherwise
     */
    static ArrayList<String> hostList(String dosURI) {
        if (Pattern.compile(":\\/\\/|/").matcher(dosURI).find()){
            return Lists.newArrayList(dosURI.split(":\\/\\/|/"));
        }
        return new ArrayList<>();
    }

    /**
     * Gets the json response from host using HTTP GET method
     *
     * @param host The targetPath split into an ArrayList object
     * @return The JSONObject containing the content of the json response
     */
    static JSONObject httpURLConnection(ArrayList<String> host) {
        StringBuilder content = null;
        HttpURLConnection con = null;
        StringBuilder sb = new StringBuilder("http://").append(host.get(1)).append("/ga4gh/dos/v1/dataobjects/").append(String.join("", host.get(2)));

        try {
            URL request = new URL(sb.toString());
            con = (HttpURLConnection) request.openConnection();
            if (con.getResponseCode() != 200) {
                sb = new StringBuilder("https://").append(host.get(1)).append("/ga4gh/dos/v1/dataobjects/").append(String.join("", host.get(2)));
                try {
                    request = new URL(sb.toString());
                    con = (HttpURLConnection) request.openConnection();
                    if (con.getResponseCode() != 200) { return null; }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                content = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.err.println("Connect error.");
            e.printStackTrace();
            return null;
        } finally {
            assert con != null;
            con.disconnect();
        }
        return new JSONObject(content.toString());
    }

}
