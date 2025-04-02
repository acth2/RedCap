package fr.acth2.redcap.backend.converters.gpx;

import fr.acth2.redcap.backend.converters.FileConverter;
import fr.acth2.redcap.backend.converters.utils.ConverterUtils;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class GpxConverter implements FileConverter {
    @Override
    public List<Map<String, Object>> convertToRecords(File inputFile) throws IOException {
        List<Map<String, Object>> records = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);

            NodeList trackpoints = document.getElementsByTagName("trkpt");
            for (int i = 0; i < trackpoints.getLength(); i++) {
                Node trackpoint = trackpoints.item(i);
                Map<String, Object> record = new HashMap<>();

                if (trackpoint.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) trackpoint;

                    record.put("latitude", Double.parseDouble(element.getAttribute("lat")));
                    record.put("longitude", Double.parseDouble(element.getAttribute("lon")));

                    parseElement(element, "time", record, "timestamp");
                    parseElement(element, "ele", record, "elevation");

                    Node extensions = getChild(element, "extensions");
                    if (extensions != null) {
                        parseExtensions((Element) extensions, record);
                    }

                    records.add(record);
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse GPX file", e);
        }

        return records;
    }

    @Override
    public Map<String, Object> calculateStatistics(List<Map<String, Object>> records) {
        return ConverterUtils.calculateCommonStatistics(records);
    }

    private void parseElement(Element parent, String tagName, Map<String, Object> record, String fieldName) {
        Node node = getChild(parent, tagName);
        if (node != null && node.getTextContent() != null && !node.getTextContent().isEmpty()) {
            try {
                record.put(fieldName, Double.parseDouble(node.getTextContent()));
            } catch (NumberFormatException e) {
                record.put(fieldName, node.getTextContent());
            }
        }
    }

    private void parseExtensions(Element extensions, Map<String, Object> record) {
        NodeList children = extensions.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                String name = element.getTagName();
                if (element.getTextContent() != null && !element.getTextContent().isEmpty()) {
                    try {
                        record.put(name.toLowerCase(), Double.parseDouble(element.getTextContent()));
                    } catch (NumberFormatException e) {
                        record.put(name.toLowerCase(), element.getTextContent());
                    }
                }
            }
        }
    }

    private Node getChild(Element parent, String tagName) {
        NodeList children = parent.getElementsByTagName(tagName);
        return children.getLength() > 0 ? children.item(0) : null;
    }
}