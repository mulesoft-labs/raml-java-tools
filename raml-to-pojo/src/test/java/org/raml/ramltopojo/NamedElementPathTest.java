package org.raml.ramltopojo;

import amf.client.model.domain.EndPoint;
import amf.client.model.domain.Parameter;
import amf.client.model.domain.ScalarShape;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created. There, you have it.
 */
public class NamedElementPathTest {

    @Test
    public void elementAt() {

        NamedElementPath p = NamedElementPath.root()
                .append(new EndPoint().withPath("/hello"))
                .append(new Parameter().withName("foo"))
                .append(new ScalarShape().withName("goo"));

        assertThat((Object)p.elementFromTheEnd(2)).isInstanceOf(EndPoint.class);
        assertThat((Object)p.elementFromTheEnd(1)).isInstanceOf(Parameter.class);
        assertThat((Object)p.elementFromTheEnd(0)).isInstanceOf(ScalarShape.class);
    }

    @Test
    public void endMatches() {

        NamedElementPath p = NamedElementPath.root()
                .append(new EndPoint().withPath("/hello"))
                .append(new Parameter().withName("foo"))
                .append(new ScalarShape().withName("goo"));

        assertThat(p.endMatches("/hello", EndPoint.class, "foo", Parameter.class, "goo", ScalarShape.class)).isTrue();
        assertThat(p.endMatches("/hello", EndPoint.class, NamedElementPath.ANY_NAME, Parameter.class, "goo", ScalarShape.class)).isTrue();
        assertThat(p.endMatches("foo", Parameter.class, "goo", ScalarShape.class)).isTrue();
        assertThat(p.endMatches("/hello", Object.class, "foo", Parameter.class, "goo", ScalarShape.class)).isTrue();

        assertThat(p.endMatches("/hello", Parameter.class, "foo", Parameter.class, "goo", ScalarShape.class)).isFalse();
        assertThat(p.endMatches("/hello", EndPoint.class, NamedElementPath.ANY_NAME, EndPoint.class, "goo", ScalarShape.class)).isFalse();
    }

}