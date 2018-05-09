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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class DOSPlugin extends Plugin {

    static PluginWrapper pluginWrapper;

//    static int hi;

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
//        List<ProvisionInterface> extensions1 = wrapper.getPluginManager().getExtensions(ProvisionInterface.class);

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

            PluginWrapper s3wrapper = pluginWrapper.getPluginManager().getPlugin("io.dockstore.provision.S3Plugin");


//            System.out.println("sourcePath: " + sourcePath);
//            System.out.println("destination: " + destination.toString());

            String trimmedPath = sourcePath.replace("dos://", "");
            List<String> splitPathList = Lists.newArrayList(trimmedPath.split("/"));
            String bucketName = splitPathList.remove(0);

            System.out.println("BUCKET NAME: " + bucketName);
//            System.out.println("Remainder: " + splitPathList);
//
            StringBuilder url = new StringBuilder("https://");

            url.append(bucketName);
            url.append("/ga4gh/dos/v1/dataobjects/");
            url.append(String.join("", splitPathList));

//            System.out.println(url);

            HttpURLConnection con = null;
            StringBuilder content = null;

            // Open connection to make HTTP request to resolve DOS Http URI
            try {
                URL request = new URL(url.toString());
                con = (HttpURLConnection) request.openConnection();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
                    String line;
                    content = new StringBuilder();
                    while((line = in.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                con.disconnect();
            }

            JSONObject jsonObj = new JSONObject(content.toString());
//            System.out.println(jsonObj.toString(2));

            JSONArray urls = jsonObj.getJSONObject("data_object").getJSONArray("urls");
            String s3 = urls.getJSONObject(1).getString("url");
            System.out.println("s3 is " + s3);

            Set<String> s3Scheme = new HashSet<>(Lists.newArrayList("s3"));

//            System.out.println("s3scheme = " + s3Scheme);
            String s = System.getProperty("pf4j.pluginsDir", "plugins");
            System.out.println(s);
            System.out.println(String.format("Found %d extensions for extension point '%s'", extensions.size(), ProvisionInterface.class.getName()));


            for (ProvisionInterface extension : extensions){
                if(extension.schemesHandled().equals(s3Scheme)) {
                    try {
                        System.out.println("---------------------- s3 is " + s3);
                        extension.downloadFrom(s3, destination);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("*********************************");
                    }
                }
            }

            return true;
        }
        public boolean uploadTo(String destPath, Path sourceFile, Optional<String> metadata) {
            System.out.println(destPath);
            System.out.println(sourceFile);
            return true;
        }
    }
}
