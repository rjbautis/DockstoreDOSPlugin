package io.dockstore.provision;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import ro.fortsoft.pf4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class DOSPlugin extends Plugin {

    private static PluginWrapper pluginWrapper;

    /**
     * Constructor to be used by plugin manager for plugin instantiation.
     * Your plugins have to provide constructor with this exact signature to
     * be successfully loaded by manager.
     *
     * @param wrapper
     */
    public DOSPlugin(PluginWrapper wrapper) {
        super(wrapper);
        pluginWrapper = wrapper;
    }

    @Override
    public void start() {
        // for testing the development mode
        if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
            System.out.println(StringUtils.upperCase("DOSPlugin development mode"));
        }
    }

    @Override
    public void stop() {
        System.out.println("DOSPlugin.stop()");
    }



    static ProgressListener getProgressListener(final long inputSize) {
        return new ProgressListener() {
            ProgressPrinter printer = new ProgressPrinter();
            long runningTotal = 0;
            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                if (progressEvent.getEventType() == ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT) {
                    runningTotal += progressEvent.getBytesTransferred();
                }
                printer.handleProgress(runningTotal, inputSize);
            }
        };
    }

    @Extension
    public static class DOSProvision implements ProvisionInterface {

        private static final String DOS_ENDPOINT = "endpoint";
        private Map<String, String> config;
        public void setConfiguration(Map<String, String> map) {
            this.config = map;
        }
        public Set<String> schemesHandled() {
            return new HashSet<>(Lists.newArrayList("dos"));
        }


        static String hostNameFromDOS(String dosURI) {
            String trimmedPath = dosURI.replace("dos://", "");
            List<String> splitPathList = Lists.newArrayList(trimmedPath.split("/"));
            return splitPathList.remove(0);
        }


        public boolean downloadFrom(String sourcePath, Path destination) {
            List<ProvisionInterface> extensions = DOSPlugin.pluginWrapper.getPluginManager().getExtensions(ProvisionInterface.class);
            HttpURLConnection con = null;
            StringBuilder content = null;
            ArrayList<String> url_list = new ArrayList<>();


            String trimmedPath = sourcePath.replace("dos://", "");
            List<String> splitPathList = Lists.newArrayList(trimmedPath.split("/"));
            String bucketName = splitPathList.remove(0);
            StringBuilder sb = new StringBuilder("http://").append(bucketName).append("/ga4gh/dos/v1/dataobjects/").append(String.join("", splitPathList));
            try {
                URL request = new URL(sb.toString());
                con = (HttpURLConnection) request.openConnection();
                if(con.getResponseCode() != 200) {
                    sb = new StringBuilder("https://").append(bucketName).append("/ga4gh/dos/v1/dataobjects/").append(String.join("", splitPathList));
                    try {
                        request = new URL(sb.toString());
                        con = (HttpURLConnection) request.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
                    String line;
                    content = new StringBuilder();
                    while((line = in.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                    }
                }
            } catch (IOException e) {
                System.err.println("Connect error.");
                e.printStackTrace();
            } finally {
                assert con != null;
                con.disconnect();
            }

            JSONObject jsonObj = new JSONObject(content.toString());
            JSONArray urls = jsonObj.getJSONObject("data_object").getJSONArray("urls");

            for(int i = 0; i < urls.length(); i++) {
                url_list.add(urls.getJSONObject(i).getString("url"));
            }
            for(String url : url_list) {
                Set<String> scheme = new HashSet<>(Lists.newArrayList(url.split("://")[0]));
                for (ProvisionInterface extension : extensions){
                    if(extension.schemesHandled().equals(scheme)) {
                        try {
                            extension.setConfiguration(config);
                            if(extension.downloadFrom(url, destination))
                                return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return false;
        }


        public boolean uploadTo(String destPath, Path sourceFile, Optional<String> metadata) {
            System.out.println(destPath);
            System.out.println(sourceFile);
            return true;
        }
    }
}
