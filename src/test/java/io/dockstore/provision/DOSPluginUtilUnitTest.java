package io.dockstore.provision;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DOSPluginUtilUnitTest {

    private static DOSPluginUtil pluginUtil = new DOSPluginUtil();

    @Test
    public void testSplitUri() {
        String uri = "dos://dos-dss.ucsc-cgp-dev.org/fff5a29f-d184-4e3b-9c5b-6f44aea7f527?version=2018-02-28T033124.129027Zf";
        ImmutableTriple<String, String, String> split =
                new ImmutableTriple<>("dos", "dos-dss.ucsc-cgp-dev.org", "fff5a29f-d184-4e3b-9c5b-6f44aea7f527?version=2018-02-28T033124.129027Zf");
        Assert.assertEquals(split, pluginUtil.splitUri(uri).get());
    }

    @Test
    public void testSplitUriMalformedPath() {
        String uri = "fake:/host//uid";
        Assert.assertFalse(pluginUtil.splitUri(uri).isPresent());
    }

    @Test
    public void testSplitUriBadPath1() {
        String uri = "fake";
        Assert.assertFalse(pluginUtil.splitUri(uri).isPresent());
    }

    @Test
    public void testSplitUriBadPath2() {
        String uri = "fake://host";
        Assert.assertFalse(pluginUtil.splitUri(uri).isPresent());
    }

    @Test
    public void testGrabJSONHttpStatusNot200() throws IOException {
        DOSPluginUtil spyPluginUtil = Mockito.spy(DOSPluginUtil.class);
        ImmutableTriple<String, String, String> split =
                new ImmutableTriple<>("fake-scheme", "fake-host", "fake-path");


        // Create mock HttpURLConnection
        HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);
        Mockito.when(mockConn.getResponseCode()).thenReturn(500);

        // Return mockConnection (with mocked response code) when createConnection() is called
        Mockito.doReturn(mockConn).when(spyPluginUtil).createConnection("http", split);
        Assert.assertFalse(spyPluginUtil.grabJSON(split).isPresent());
    }

    @Test
    public void testGrabJSONHttpsStatusNot200() throws IOException {
        DOSPluginUtil spyPluginUtil = Mockito.spy(DOSPluginUtil.class);
        ImmutableTriple<String, String, String> split =
                new ImmutableTriple<>("fake-scheme", "fake-host", "fake-path");

        // Create mock HttpURLConnection
        HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);
        Mockito.when(mockConn.getResponseCode()).thenReturn(500).thenReturn(500);

        // Return mockConnection (with mocked response code) when createConnection() is called
        Mockito.doReturn(mockConn).when(spyPluginUtil).createConnection("http", split);
        Mockito.doReturn(mockConn).when(spyPluginUtil).createConnection("https", split);

        Assert.assertFalse(spyPluginUtil.grabJSON(split).isPresent());
    }

    @Test
    public void testGrabJSONBadURI() {
        ImmutableTriple<String, String, String> split =
                new ImmutableTriple<>("fake-scheme", "fake-host", "fake-path");
        Assert.assertFalse(pluginUtil.grabJSON(split).isPresent());
    }

    @Test
    public void testCreateConnection() throws IOException {
        ImmutableTriple<String, String, String> split =
                new ImmutableTriple<>("dos", "ec2-52-26-45-130.us-west-2.compute.amazonaws.com:8080", "911bda59-b6f9-4330-9543-c2bf96df1eca");

        URL mockURL = new URL("http://" + split.getMiddle() + "/ga4gh/dos/v1/dataobjects/" + split.getRight());
        HttpURLConnection mockConn = (HttpURLConnection) mockURL.openConnection();
        Assert.assertThat(mockConn.toString(), CoreMatchers.containsString(pluginUtil.createConnection("http", split).toString()));
    }

    @Test
    public void testCreateConnectionReturnNull() {
        ImmutableTriple<String, String, String> split =
                new ImmutableTriple<>("fake-scheme", "fake-host", "fake-path");

        Assert.assertNull(pluginUtil.createConnection("fake-protocol", split));
    }

    @Test
    public void testReadResponse() throws IOException {
        ImmutableTriple<String, String, String> split =
                new ImmutableTriple<>("dos", "ec2-52-26-45-130.us-west-2.compute.amazonaws.com:8080", "911bda59-b6f9-4330-9543-c2bf96df1eca");

        HttpURLConnection actualConn = pluginUtil.createConnection("http", split);

        InputStream expectedResponse = IOUtils.toInputStream("{  \"data_object\": {    \"aliases\": [      " +
                "\"phase3\",      \"data\",      \"HG03237\",      \"cg_data\",      \"ASM_blood\",      \"REPORTS\"," +
                "      \"substitutionLengthCoding-GS000017140-ASM.tsv\",      " +
                "\"phase3/data/HG03237/cg_data/ASM_blood/REPORTS/substitutionLengthCoding-GS000017140-ASM.tsv\"    ]," +
                "    \"checksums\": [      {        \"checksum\": \"ff7d7ec9a803e09ffab681165a9b7c36\",        " +
                "\"type\": \"md5\"      }    ],    \"created\": \"2015-05-21T23:09:20+00:00\",    \"current\": true, " +
                "   \"id\": \"911bda59-b6f9-4330-9543-c2bf96df1eca\",    \"size\": \"491\",    \"updated\": " +
                "\"2015-05-21T23:09:20+00:00\",    \"urls\": [      {        \"system_metadata\": {          " +
                "\"StorageClass\": \"STANDARD\",          \"bucket_name\": \"1000genomes\",          \"event_type\": " +
                "\"ObjectCreated:Put\"        },        \"url\": " +
                "\"s3://1000genomes/phase3/data/HG03237/cg_data/ASM_blood/REPORTS/substitutionLengthCoding" +
                "-GS000017140-ASM.tsv\",        \"user_metadata\": {          \"s3cmd-attrs\": " +
                "\"uid:5343/gname:sysadmin/uname:meslerd/gid:14/mode:33188/mtime:1432249758/atime:1432249758/md5" +
                ":ff7d7ec9a803e09ffab681165a9b7c36/ctime:1432249758\"        }      }    ],    \"version\": " +
                "\"2018-05-01T05:44:57.781598Z\"  }}", "UTF-8");
        HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);
        Mockito.when(mockConn.getInputStream()).thenReturn(expectedResponse);
        BufferedReader bufferedReader = Mockito.spy(new BufferedReader(new InputStreamReader(mockConn.getInputStream ())));
        String line;
        StringBuilder mockContent = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            mockContent.append(line);
        }
        Assert.assertEquals(mockContent.toString(), pluginUtil.readResponse(actualConn.getInputStream()));
    }
}
