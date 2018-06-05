package io.dockstore.provision;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import ro.fortsoft.pf4j.*;

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

    @Extension
    public static class DOSPreProvision implements PreProvisionInterface {

        public static final int SCHEME = 0;

        public Set<String> schemesHandled() {
            return new HashSet<>(Lists.newArrayList("dos"));
        }

        public List<String> prepareDownload(String targetPath) {
            DOSPluginUtil pluginUtil = new DOSPluginUtil();
            List<String> url_list = new ArrayList<>();
            List<String> uri = pluginUtil.splitUri(targetPath);

            if (!uri.isEmpty() && schemesHandled().contains(uri.get(SCHEME))) {
                JSONObject jsonObj = pluginUtil.grabJSON(uri);
                if(jsonObj == null) {
                    return url_list;
                }
                JSONArray urls = jsonObj.getJSONObject("data_object").getJSONArray("urls");
                for (int i = 0; i < urls.length(); i++) {
                    url_list.add(urls.getJSONObject(i).getString("url"));
                }
            }
            return url_list;
        }
    }
}
