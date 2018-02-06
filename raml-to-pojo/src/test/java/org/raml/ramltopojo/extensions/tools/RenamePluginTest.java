package org.raml.ramltopojo.extensions.tools;

import com.squareup.javapoet.ClassName;
import org.junit.Test;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class RenamePluginTest {

    @Test
    public void className() {

        RenamePlugin plugin = new RenamePlugin(Arrays.asList("One", "OneImplementation"));
        ClassName cn  = plugin.className((ObjectPluginContext) null, null, ClassName.bestGuess("fun.com.Allo"), EventType.INTERFACE);
        assertEquals("fun.com.One", cn.toString());

        cn  = plugin.className((ObjectPluginContext) null, null, ClassName.bestGuess("fun.com.Allo"), EventType.IMPLEMENTATION);
        assertEquals("fun.com.OneImplementation", cn.toString());
    }

    @Test
    public void classNameWithDefaultImpl() {

        RenamePlugin plugin = new RenamePlugin(Arrays.asList("One"));
        ClassName cn  = plugin.className((ObjectPluginContext) null, null, ClassName.bestGuess("fun.com.Allo"), EventType.INTERFACE);
        assertEquals("fun.com.One", cn.toString());

        cn  = plugin.className((ObjectPluginContext) null, null, ClassName.bestGuess("fun.com.Allo"), EventType.IMPLEMENTATION);
        assertEquals("fun.com.OneImpl", cn.toString());
    }
}