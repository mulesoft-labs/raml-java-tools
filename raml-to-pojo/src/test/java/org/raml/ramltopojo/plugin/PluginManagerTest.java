package org.raml.ramltopojo.plugin;

import org.junit.Test;
import org.raml.ramltopojo.object.ObjectTypeHandlerPlugin;

import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class PluginManagerTest {

    @Test
    public void getExtension() throws Exception {

        PluginManager em = PluginManager.createPluginManager("org/raml/ramltopojo/plugin/test1.properties");
        Set<ObjectTypeHandlerPlugin> list = em.getClassesForName("core.one", ObjectTypeHandlerPlugin.class);

        assertEquals(PluginOne.class, list.iterator().next().getClass());
    }

    @Test
    public void twoClasses() throws Exception {

        PluginManager em = PluginManager.createPluginManager("org/raml/ramltopojo/plugin/test1.properties");
        Set<ObjectTypeHandlerPlugin> list1 = em.getClassesForName("core.sub.two", ObjectTypeHandlerPlugin.class);
        assertEquals(1, list1.size());
        assertEquals(PluginOne.class, list1.iterator().next().getClass());
    }


}