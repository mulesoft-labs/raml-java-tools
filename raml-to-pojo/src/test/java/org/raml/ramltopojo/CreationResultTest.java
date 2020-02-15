package org.raml.ramltopojo;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.raml.testutils.UnitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created. There, you have it.
 */
public class CreationResultTest extends UnitTest{

    TypeSpec interf = TypeSpec.classBuilder("foo").build();

    TypeSpec cls = TypeSpec.classBuilder("goo").build();

    @Test
    public void createType() throws Exception {

        CreationResult result = new CreationResult(null, "pack.me", ClassName.get("", "foo"), ClassName.get("", "foo")) {

            TypeSpec[] specs = {interf, cls};
            int c = 0;
            @Override
            protected void createJavaFile(String pack, TypeSpec spec, String root, boolean b) {

                assertEquals("pack.me", pack);
                assertEquals("/tmp/foo", root);
                assertSame(spec, specs[c ++]);
            }
        };

        result.withInterface(interf).withImplementation(cls);
        result.createType("/tmp/foo");
    }

}