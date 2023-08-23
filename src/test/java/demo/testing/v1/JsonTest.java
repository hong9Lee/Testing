package demo.testing.v1;

import co.hong.libs.json.JsonUtil;
import jdk.jfr.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Map;

public class JsonTest {

    @Test
    @Description("string json을 파싱하여 값을 비교할 수 있다.")
    void getJson() {
        String jsonString = "{\"version\":1}";
        Map<String, Object> stringObjectMap = JsonUtil.parseAsMap(jsonString);
        Object version = stringObjectMap.get("version");
        Assertions.assertEquals(version, 1);
    }
}
