package io.dockstore.provision;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

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
    public void testPrepareDownload() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
        List<String> expected = new ArrayList<>();
        String targetPath = "dos://dos-dss.ucsc-cgp-dev.org/fff5a29f-d184-4e3b-9c5b-6f44aea7f527?version=2018-02-28T033124.129027Zf";
        expected.add("gs://topmed-irc-share/genomes/NWD106415.b38.irc.v1.cram");
        expected.add("s3://nih-nhlbi-datacommons/NWD106415.b38.irc.v1.cram");
        Assert.assertEquals(expected, dos.prepareDownload(targetPath));
    }

    @Test
    public void testPrepareDownload2() {
        DOSPlugin.DOSPreProvision dos = new DOSPlugin.DOSPreProvision();
        List<String> expected2 = new ArrayList<>();
        String targetPath = "dos://ec2-52-26-45-130.us-west-2.compute.amazonaws.com:8080/911bda59-b6f9-4330-9543-c2bf96df1eca";
        expected2.add("s3://1000genomes/phase3/data/HG03237/cg_data/ASM_blood/REPORTS/substitutionLengthCoding-GS000017140-ASM.tsv");
        Assert.assertEquals(expected2, dos.prepareDownload(targetPath));
    }

}


