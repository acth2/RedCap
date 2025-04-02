package fr.acth2.redcap.backend.converters.fit;

import com.garmin.fit.*;
import fr.acth2.redcap.backend.converters.FileConverter;
import fr.acth2.redcap.backend.math.StatisticsCalculator;
import org.apache.commons.csv.*;
import java.io.*;
import java.io.File;
import java.util.*;

import static fr.acth2.redcap.utils.References.*;

public class FitConverter implements FileConverter {
    @Override
    public List<Map<String, Object>> convertToRecords(File inputFile) throws IOException {
        List<Map<String, Object>> records = new ArrayList<>();
        Decode decode = new Decode();
        MesgBroadcaster mesgBroadcaster = new MesgBroadcaster(decode);
        FitListener listener = new FitListener();

        mesgBroadcaster.addListener(listener);
        try {
            mesgBroadcaster.run(new FileInputStream(inputFile));
            records = listener.getRecords();
        } catch (FitRuntimeException e) {
            throw new IOException("Failed to decode FIT file", e);
        }

        return records;
    }

    @Override
    public Map<String, Object> calculateStatistics(List<Map<String, Object>> records) {
        return StatisticsCalculator.calculateStatistics(records);
    }
    private static class FitListener implements MesgListener {
        private final List<Map<String, Object>> records = new ArrayList<>();
        private final Set<String> headers = new LinkedHashSet<>();

        @Override
        public void onMesg(Mesg mesg) {
            Map<String, Object> record = new HashMap<>();
            for (Field field : mesg.getFields()) {
                String fieldName = mesg.getName() + "_" + field.getName();
                Object value = field.getValue();

                try {
                    value = getFieldScale(field);
                } catch (FitRuntimeException e) {
                }

                record.put(fieldName, value);
                headers.add(fieldName);
            }
            records.add(record);
        }

        public List<Map<String, Object>> getRecords() {
            return records;
        }
    }
}