package com.airhacks.wad.control;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class WarNameProvider {

    public static String getCustomWarName(Path pathToPom) throws IOException {
        String warName = getDefaultWarName();

        Optional<String> finalName = getProjectNameFromPom(pathToPom);

        if (finalName.isPresent() && finalName.get().trim().length() > 0) {
            warName = finalName.get() + ".war";
        }

        return warName;
    }

    private static String getDefaultWarName() {
        Path currentPath = Paths.get("").toAbsolutePath();
        Path currentDirectory = currentPath.getFileName();
        return currentDirectory + ".war";
    }

    private static Optional<String> getProjectNameFromPom(final Path pom) throws IOException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File pomXml = pom.toFile();
            Document document = builder.parse(new InputSource(new FileReader(pomXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String result = (String) xPath.evaluate("/project/build/finalName", document, XPathConstants.STRING);
            return Optional.of(result);
        } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
            return Optional.empty();
        }
    }
}
