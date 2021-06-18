package uorocketry.basestation.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uorocketry.basestation.Main;
import uorocketry.basestation.config.Config;
import uorocketry.basestation.config.DataSet;
import uorocketry.basestation.config.FakeConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataProcessorUnitTest {

    Config config;

    @BeforeEach
    public void setup() {
        String[] labels = new String[] {"Timestamp (ns)", "Value 1", "Value 2", "Value 3", "Value 4", "Value 5", "Value 6", "Value 7", "Value 8", "Value 9"};
        String[] states = new String[] {"First State", "Second State"};
        Map<String, Integer> indexes = new HashMap<>();
        indexes.put("timestamp", 0);

        DataSet dataSet = new DataSet("Processor Testing Set", "#AC1C3A", labels, states, indexes, ",");
        config = new FakeConfig(Collections.singletonList(dataSet), null);
    }

    @Test
    public void parseData_plain() {
        String data = "102020399293,2,182.12,192,12.41,2,1,331,12,1";
        assertAndParseData(setupParseDataConfig(), data);
    }

    @Test
    public void parseData_newline() {
        String data = "102020399293,2,182.12,192,12.41,2,1,331,12,1\r\n";
        assertAndParseData(setupParseDataConfig(), data);
    }

    @Test
    public void parseData_plainNewline() {
        String data = "102020399293,2,182.12,192,12.41,2,1,331,12,1\\r\\n";
        assertAndParseData(setupParseDataConfig(), data);
    }

    @Test
    public void parseData_plainNewlineAndNewline() {
        String data = "102020399293,2,182.12,192,12.41,2,1,331,12,1\\r\\n\r\n";
        assertAndParseData(setupParseDataConfig(), data);
    }

    @Test
    public void parseData_binarySyntax() {
        String data = "b'102020399293,2,182.12,192,12.41,2,1,331,12,1\\r\\n'";
        assertAndParseData(setupParseDataConfig(), data);
    }

    private DataProcessor setupParseDataConfig() {
        return new DataProcessor(config, null);
    }

    private void assertAndParseData(DataProcessor testObject, String data) {
        DataHolder result = testObject.parseData(0, data);

        assertEquals(102020399293L, result.data[0].getLongValue());
        assertEquals("102,020,399,293", result.data[0].getFormattedString());
        assertEquals(2, result.data[1].getDecimalValue());
        assertEquals(182.12f, result.data[2].getDecimalValue());
        assertEquals(192, result.data[3].getDecimalValue());
        assertEquals(12.41f, result.data[4].getDecimalValue());
        assertEquals(2, result.data[5].getDecimalValue());
        assertEquals(1, result.data[6].getDecimalValue());
        assertEquals("1", result.data[6].getFormattedString());
        assertEquals(331, result.data[7].getDecimalValue());
        assertEquals(12, result.data[8].getDecimalValue());
        assertEquals(1, result.data[9].getDecimalValue());
    }
}
