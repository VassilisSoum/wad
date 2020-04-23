
package wad;

import com.airhacks.wad.boundary.WADFlow;
import com.airhacks.wad.control.Configurator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.airhacks.wad.control.PreBuildChecks.*;

/**
 *
 * @author airhacks.com
 */
public class App {
    
    static Path addTrailingSlash(String path) {
        if (!path.endsWith(File.separator)) {
            return Paths.get(path, File.separator);
        }
        return Paths.get(path);
    }

    static void printWelcomeMessage() throws IOException {
        try (InputStream resourceAsStream = App.class.
                getClassLoader().
                getResourceAsStream("META-INF/maven/com.airhacks/wad/pom.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            String wad = properties.getProperty("artifactId");
            String version = properties.getProperty("version");
            System.out.println(wad + " " + version);
        }
    }

    static List<Path> convert(String[] args) {
        return Arrays.stream(args).map(App::addTrailingSlash).collect(Collectors.toList());
    }

    static List<Path> addWarName(Set<Path> deploymentDirectories, String warName) {
        return deploymentDirectories.
                stream().
                map(path -> path.resolve(warName)).
                collect(Collectors.toList());
    }

    static Optional<String> getCustomSetWarName(String[] args) {
        Optional<String> customSetWarName = Arrays.stream(args).filter(arg -> arg.startsWith("warName=")).findFirst();
        return customSetWarName.map(s -> s.split("=")[1]);
    }

    public static void main(String[] args) throws IOException {
        printWelcomeMessage();
        if (args.length < 1 && !Configurator.userConfigurationExists()) {
            System.out.println("Invoke with java -jar wad.jar [DEPLOYMENT_DIR1,DEPLOYMENT_DIR1] or create ~/.wadrc");
            System.exit(-1);
        }
        pomExists();

        Path thinWARPath = null;
        String thinWARName = null;
        Optional<String> customSetWarName = getCustomSetWarName(args);
        if (customSetWarName.isPresent()) {
            try {
                thinWARPath = Paths.get("target", customSetWarName.get());
            } catch (Exception e) {
                exit();
            }
        } else {
            Path currentPath = Paths.get("").toAbsolutePath();
            Path currentDirectory = currentPath.getFileName();
            thinWARName = currentDirectory + ".war";
            thinWARPath = Paths.get("target", thinWARName);
        }

        Set<Path> deploymentDirs = Configurator.getConfiguredFolders(convert(args));
        validateDeploymentDirectories(deploymentDirs);

        List<Path> deploymentTargets = addWarName(deploymentDirs, thinWARName);
        Path sourceCodeDir = Paths.get("./src/main/");
        System.out.printf("WAD is watching %s, deploying %s to %s \n", sourceCodeDir, thinWARPath, deploymentTargets);
        WADFlow wadFlow = new WADFlow(sourceCodeDir, thinWARPath, deploymentTargets);
    }

}
