package io.dockstore.provision;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DOSPluginUnitTest {

    @Test
    public void testDOSProvision() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
    }


    @Test
    public void testSchemesHandled() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
        Set<String> scheme = new HashSet<>(Collections.singletonList("dos"));
        Assert.assertEquals(scheme, dos.schemesHandled());
    }

    @Test
    public void testSchemesHandledFailed() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
        Set<String> scheme = new HashSet<>(Collections.singletonList("fake"));
        Assert.assertNotEquals(scheme, dos.schemesHandled());
    }


    @Test
    public void testPrepareDownload() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
        List<String> expected = new ArrayList<>();
        String targetPath = "dos://ec2-52-26-45-130.us-west-2.compute.amazonaws.com:8080/911bda59-b6f9-4330-9543-c2bf96df1eca";
        expected.add("s3://1000genomes/phase3/data/HG03237/cg_data/ASM_blood/REPORTS/substitutionLengthCoding-GS000017140-ASM.tsv");
        Assert.assertEquals(expected, dos.prepareDownload(targetPath));
    }


    @Test
    public void testPrepareDownloadReturnEmpty1() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
        String targetPath = "dos://dos-dss.ucsc-cgp-dev.org/fff5a29f-d184-4e3b-9c5b-6f44aea7f527?version=2018-02-28T033124.129027Zf";
        Assert.assertTrue(dos.prepareDownload(targetPath).isEmpty());
    }


    @Test
    public void testPrepareDownloadReturnEmpty2() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
        String targetPath = "fake";
        Assert.assertTrue(dos.prepareDownload(targetPath).isEmpty());
    }


    @Test
    public void testPrepareDownloadReturnEmpty3() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
        String targetPath = "dos:/fake";
        Assert.assertTrue(dos.prepareDownload(targetPath).isEmpty());
    }
}
