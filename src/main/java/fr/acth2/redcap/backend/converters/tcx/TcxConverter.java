package fr.acth2.redcap.backend.converters.tcx;

import fr.acth2.redcap.backend.converters.FileConverter;
import fr.acth2.redcap.backend.converters.utils.ConverterUtils;
import fr.acth2.redcap.backend.math.StatisticsCalculator;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class TcxConverter implements FileConverter {
    @Override
    public List<Map<String, Object>> convertToRecords(File inputFile) throws IOException {
        List<Map<String, Object>> records = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);

            NodeList trackpoints = document.getElementsByTagName("Trackpoint");
            for (int i = 0; i < trackpoints.getLength(); i++) {
                Node trackpoint = trackpoints.item(i);
                Map<String, Object> record = new HashMap<>();

                if (trackpoint.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) trackpoint;

                    parseElement(element, "Time", record, "time");
                    parseElement(element, "DistanceMeters", record, "distance");
                    parseElement(element, "HeartRateBpm", record, "heart_rate");

                    Node position = getChild(element, "Position");
                    if (position != null) {
                        parseElement((Element) position, "LatitudeDegrees", record, "latitude");
                        parseElement((Element) position, "LongitudeDegrees", record, "longitude");
                    }

                    Node extensions = getChild(element, "Extensions");
                    if (extensions != null) {
                        parseExtensions((Element) extensions, record);
                    }

                    records.add(record);
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse TCX file", e);
        }

        return records;
    }

    @Override
    public Map<String, Object> calculateStatistics(List<Map<String, Object>> records) {
        return StatisticsCalculator.calculateStatistics(records);
    }

    private void parseElement(Element parent, String tagName, Map<String, Object> record, String fieldName) {
        Node node = getChild(parent, tagName);
        if (node != null && node.getTextContent() != null && !node.getTextContent().isEmpty()) {
            try {
                String content = node.getTextContent();
                if (content.matches("-?\\d+(\\.\\d+)?")) {
                    record.put(fieldName, Double.parseDouble(content));
                } else {
                    record.put(fieldName, content);
                }
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