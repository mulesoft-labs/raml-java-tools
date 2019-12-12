package foo.foo.impl;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import foo.foo.Date1UnionImpl;
import foo.foo.Date2UnionImpl;
import foo.foo.Date3UnionImpl;
import foo.foo.DateUnion;
import foo.foo.DateUnionImpl;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnionsWithDatesTest {
    
    @Test
    public void dateTest() throws IOException {
        
        DateUnion dateUnion = new DateUnionImpl();
        dateUnion.setDate1(new Date1UnionImpl(new Date(1234567)));
        dateUnion.setDate2(new Date2UnionImpl(new Date(1234567)));
        dateUnion.setDate3(new Date3UnionImpl(new Date(1234567)));
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dateUnion);
                        
        JsonNode node = mapper.readTree(json);
        
        assertTrue(node.get("date1").isTextual() && node.get("date1").asText().equals("1970-20-01"));
        assertTrue(node.get("date2").isTextual() && node.get("date2").asText().equals("01:20:34"));
        assertTrue(node.get("date3").isTextual() && node.get("date3").asText().equals("1970-01-01T01:20:34"));
        
        DateUnion dateUnionTemp = mapper.readValue(json, DateUnion.class);
        
        assertTrue(dateUnionTemp.getDate1().isDate());
        assertTrue(dateUnionTemp.getDate2().isDate());
        assertTrue(dateUnionTemp.getDate3().isDate());
    }
}
