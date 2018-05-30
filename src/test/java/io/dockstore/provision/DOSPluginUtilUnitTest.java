package io.dockstore.provision;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;


public class DOSPluginUtilUnitTest {

    @Test
    public void testHostList() {
        String uri = "dos://dos-dss.ucsc-cgp-dev.org/fff5a29f-d184-4e3b-9c5b-6f44aea7f527?version=2018-02-28T033124.129027Zf";
        ArrayList<String> expected = new ArrayList<>();

        expected.add("dos");
        expected.add("dos-dss.ucsc-cgp-dev.org");
        expected.add("fff5a29f-d184-4e3b-9c5b-6f44aea7f527?version=2018-02-28T033124.129027Zf");

        Assert.assertEquals(expected, DOSPluginUtil.hostList(uri));
    }


    @Test
    public void testHostListMalformedPath() {
        String uri = "fake:/path";
        Assert.assertTrue(DOSPluginUtil.hostList(uri).isEmpty());
    }

    @Test
    public void testHostListBadPath() {
        String uri = "fake://path";
        Assert.assertTrue(DOSPluginUtil.hostList(uri).isEmpty());
    }


    @Test
    public void testHttpURLConnectionError() {
        ArrayList<String> expected = new ArrayList<>();

        expected.add("fake-uri");
        expected.add("fake-site");
        expected.add("fake-path");

        Assert.assertNull(DOSPluginUtil.httpURLConnection(expected));
    }
}
