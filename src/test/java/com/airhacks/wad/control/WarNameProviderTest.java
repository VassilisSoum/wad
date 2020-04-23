package com.airhacks.wad.control;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class WarNameProviderTest {

    @Test
    public void finalNameInPomIsNotSet() throws IOException {
        Path pomWithoutFinalNamePath = Paths.get("src/test/resources", "pom-without-name.xml");
        Path currentPath = Paths.get("").toAbsolutePath();
        Path currentDirectory = currentPath.getFileName();
        String thinWARName = currentDirectory + ".war";

        String thinWarName = WarNameProvider.getCustomWarName(pomWithoutFinalNamePath);
        assertThat(thinWarName, equalTo(thinWARName));
    }

    @Test
    public void finalNameInPomIsSet() throws IOException {
        Path pomWithFinalNamePath = Paths.get("src/test/resources", "pom-with-name.xml");

        String thinWarName = WarNameProvider.getCustomWarName(pomWithFinalNamePath);
        assertThat(thinWarName, equalTo("custom.war"));
    }
}