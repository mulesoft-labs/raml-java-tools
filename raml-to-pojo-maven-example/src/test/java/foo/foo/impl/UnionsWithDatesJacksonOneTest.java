package foo.foo.impl;

import foo.foo.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class UnionsWithDatesJacksonOneTest {

    @Test
    public void dateTest() throws IOException {
        
        DateUnionJ1 dateUnion = new DateUnionJ1Impl();
        dateUnion.setDate1(new Date1UnionJ1Impl(new Date(1234567)));
        dateUnion.setDate2(new Date2UnionJ1Impl(new Date(1234567)));
        dateUnion.setDate3(new Date3UnionJ1Impl(new Date(1234567)));
        dateUnion.setDate4(new Date4UnionJ1Impl(new Date(1234567)));
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dateUnion);
                        
        JsonNode node = mapper.readTree(json);
        assertTrue(node.get("date1").isTextual() && node.get("date1").asText().equals(new SimpleDateFormat("yyyy-mm-dd").format(new Date(1234567))));
        assertTrue(node.get("date2").isTextual() && node.get("date2").asText().equals(new SimpleDateFormat("hh:mm:ss").format(new Date(1234567))));
        assertTrue(node.get("date3").isTextual() && node.get("date3").asText().equals(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date(1234567))));
        assertTrue(node.get("date4").isTextual() && node.get("date4").asText().equals(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date(1234567))));

        
        DateUnionJ1 dateUnionTemp = mapper.readValue(json, DateUnionJ1.class);
        
        assertTrue(dateUnionTemp.getDate1().isDate());
        assertTrue(dateUnionTemp.getDate2().isDate());
        assertTrue(dateUnionTemp.getDate3().isDate());
        assertTrue(dateUnionTemp.getDate4().isDate());
    }
}
