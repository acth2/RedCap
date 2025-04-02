package fr.acth2.redcap.backend.converters.utils;

import java.util.*;

public class ConverterUtils {
    public static Map<String, Object> calculateCommonStatistics(List<Map<String, Object>> records) {
        Map<String, Object> stats = new HashMap<>();
        if (records.isEmpty()) return stats;

        Set<String> numericFields = new HashSet<>();
        records.get(0).keySet().forEach(field -> {
            if (records.get(0).get(field) instanceof Number) {
                numericFields.add(field);
            }
        });

        for (String field : numericFields) {
            DoubleSummaryStatistics summary = records.stream()
                    .filter(r -> r.get(field) instanceof Number)
                    .mapToDouble(r -> ((Number) r.get(field)).doubleValue())
                    .summaryStatistics();

            if (summary.getCount() > 0) {
                stats.put(field + "_count", summary.getCount());
                stats.put(field + "_avg", summary.getAverage());
                stats.put(field + "_min", summary.getMin());
                stats.put(field + "_max", summary.getMax());
                stats.put(field + "_sum", summary.getSum());
            }
        }

        return stats;
    }
}