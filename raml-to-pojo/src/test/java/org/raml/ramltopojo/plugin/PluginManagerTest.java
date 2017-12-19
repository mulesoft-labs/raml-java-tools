package org.raml.ramltopojo.plugin;

import org.junit.Test;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class PluginManagerTest {

    @Test
    public void getExtension() throws Exception {

        PluginManager em = PluginManager.createPluginManager("org/raml/ramltopojo/plugin/test1.properties");
        Set<ObjectTypeHandlerPlugin> list = em.getClassesForName("core.one", Collections.<String>emptyList(), ObjectTypeHandlerPlugin.class);

        assertEquals(PluginOne.class, list.iterator().next().getClass());
    }

    @Test
    public void twoDifferentClasses() throws Exception {

        PluginManager em = PluginManager.createPluginManager("org/raml/ramltopojo/plugin/test1.properties");
        Set<ObjectTypeHandlerPlugin> list1 = em.getClassesForName("core.sub.two", Collections.<String>emptyList(), ObjectTypeHandlerPlugin.class);
        assertEquals(1, list1.size());
        assertEquals(PluginOne.class, list1.iterator().next().getClass());
    }


    @Test
    public void classWithList() throws Exception {

        PluginManager em = PluginManager.createPluginManager("org/raml/ramltopojo/plugin/test1.properties");
        Set<ObjectTypeHandlerPlugin> list1 = em.getClassesForName("core.withArgs", Arrays.asList("1", "2"), ObjectTypeHandlerPlugin.class);
        assertEquals(1, list1.size());
        assertEquals(PluginMoreOne.class, list1.iterator().next().getClass());
        assertEquals(Arrays.asList("1", "2"), ((PluginMoreOne)list1.iterator().next()).getArguments());
    }


}