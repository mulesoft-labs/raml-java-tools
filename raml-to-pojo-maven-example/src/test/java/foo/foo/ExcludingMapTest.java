package foo.foo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created. There, you have it.
 */
public class ExcludingMapTest {

    @Test
    public void addsStuff() throws Exception {

        ExcludingMap em = new ExcludingMap();
        em.addAcceptedPattern(Pattern.compile("^note\\d+$"));

        em.put("note1", "one");
        assertEquals("one", em.get("note1"));
    }

    @Test
    public void addInvalid() throws Exception {

        ExcludingMap em = new ExcludingMap();
        em.addAcceptedPattern(Pattern.compile("^note\\d+$"));

        try {
            em.put("boo", "one");
            fail("should fail");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void addInvalidFromMap() throws Exception {

        ExcludingMap em = new ExcludingMap();
        em.addAcceptedPattern(Pattern.compile("^note\\d+$"));

        Map<String, Object> otherMap = new HashMap<>();
        otherMap.put("boo", "one");
        try {
            em.putAll(otherMap);
            fail("should fail");
        } catch (IllegalArgumentException e) {

        }
    }

}
