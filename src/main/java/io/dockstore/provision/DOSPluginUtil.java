package io.dockstore.provision;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_OK;


class DOSPluginUtil {

    private static final String API = "/ga4gh/dos/v1/dataobjects/";
    private static final int SCHEME = 0;
    private static final int HOST = 1;
    private static final int PATH = 2;

    // Package-private constructor
    DOSPluginUtil() {
    }

    /**
     *
     *
     * @param dosURI The string targetPath
     * @return the scheme, host, and path of the targetPath, or <code>Optional.empty()</code>
     */
    Optional<ImmutableTriple<String, String, String>> splitUri(String dosURI) {
        if (Pattern.compile(":\\/\\/(.+)/").matcher(dosURI).find()){
            List<String> split  = Lists.newArrayList(dosURI.split(":\\/\\/|/"));
            return Optional.ofNullable(new ImmutableTriple<>(split.get(SCHEME), split.get(HOST), split.get(PATH)));
        }
        return Optional.empty();
    }

    /**
     * Gets the JSON response from targetPath using HTTP GET request
     *
     * @param immutableTriple The targetPath as an ImmutableTriple of <scheme, host, path>
     * @return The JSONObject containing the content of the JSON response, or <code>Optional.empty()</code>
     */
    Optional<JSONObject> grabJSON(ImmutableTriple<String, String, String> immutableTriple){
        String content;
        HttpURLConnection conn = null;

        try {
            conn = createConnection("http", immutableTriple);
            if (Objects.requireNonNull(conn).getResponseCode() != HTTP_OK) {
                try {
                    conn = createConnection("https", immutableTriple);
                    if (Objects.requireNonNull(conn).getResponseCode() != HTTP_OK) { return Optional.empty(); }
                } catch (IOException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            }
            content = readResponse(conn.getInputStream());
            return Optional.of(new JSONObject(content));
        } catch (Exception e) {
            System.err.println("Plugin HttpURLConnection error: "  + e.getCause());
            e.printStackTrace();
        } finally {
            assert conn != null;
            conn.disconnect();
        }
        return Optional.empty();
    }

    HttpURLConnection createConnection(String protocol, ImmutableTriple<String, String, String> immutableTriple) {
        try {
            URL request = new URL(protocol + "://" + immutableTriple.getMiddle() + API +  immutableTriple.getRight());
            return (HttpURLConnection) request.openConnection();
        } catch ( IOException e) {
            System.err.println("ERROR opening HTTP URL Connection.");
            e.printStackTrace();
        }
        return null;
    }

    String readResponse(InputStream stream) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))){
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
