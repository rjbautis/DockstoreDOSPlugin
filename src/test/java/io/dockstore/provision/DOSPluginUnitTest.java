package io.dockstore.provision;

import io.dockstore.provision.DOSPlugin;
import org.junit.Assert;
import org.junit.Test;

public class DOSPluginUnitTest {

    @Test
    public void testHostNameFromDOS() {
        String uri = "dos://dos-dss.ucsc-cgp-dev.org/fff5a29f-d184-4e3b-9c5b-6f44aea7f527?version=2018-02-28T033124.129027Zf";
        Assert.assertEquals("dos-dss.ucsc-cgp-dev.org", DOSPlugin.DOSProvision.hostNameFromDOS(uri));
    }

    @Test
    public void testDOSProvision() {
        DOSPlugin.DOSProvision dos = new DOSPlugin.DOSProvision();
    }

}
