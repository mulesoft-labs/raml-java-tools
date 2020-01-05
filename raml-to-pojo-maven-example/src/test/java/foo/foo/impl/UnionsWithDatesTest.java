package foo.foo.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import foo.foo.*;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class UnionsWithDatesTest {

    @Test
    public void dateTest() throws IOException {
        
        DateUnion dateUnion = new DateUnionImpl();
        dateUnion.setDate1(new Date1UnionImpl(new Date(1234567)));
        dateUnion.setDate2(new Date2UnionImpl(new Date(1234567)));
        dateUnion.setDate3(new Date3UnionImpl(new Date(1234567)));
        dateUnion.setDate4(new Date4UnionImpl(new Date(1234567)));
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dateUnion);
                        
        JsonNode node = mapper.readTree(json);
        assertTrue(node.get("date1").isTextual() && node.get("date1").asText().equals(new SimpleDateFormat("yyyy-mm-dd").format(new Date(1234567))));
        assertTrue(node.get("date2").isTextual() && node.get("date2").asText().equals(new SimpleDateFormat("hh:mm:ss").format(new Date(1234567))));
        assertTrue(node.get("date3").isTextual() && node.get("date3").asText().equals(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date(1234567))));
        assertTrue(node.get("date4").isTextual() && node.get("date4").asText().equals(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date(1234567))));

        
        DateUnion dateUnionTemp = mapper.readValue(json, DateUnion.class);
        
        assertTrue(dateUnionTemp.getDate1().isDate());
        assertTrue(dateUnionTemp.getDate2().isDate());
        assertTrue(dateUnionTemp.getDate3().isDate());
        assertTrue(dateUnionTemp.getDate4().isDate());
    }

}
