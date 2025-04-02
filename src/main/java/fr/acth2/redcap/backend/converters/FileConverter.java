package fr.acth2.redcap.backend.converters;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileConverter {
    List<Map<String, Object>> convertToRecords(File inputFile) throws IOException;
    Map<String, Object> calculateStatistics(List<Map<String, Object>> records);
}