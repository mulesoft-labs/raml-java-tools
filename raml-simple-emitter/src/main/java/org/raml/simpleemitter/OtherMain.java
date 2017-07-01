package org.raml.simpleemitter;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 * Created. There, you have it.
 */
public class OtherMain {

    public static void main(String[] args) {


        Yaml yaml = new Yaml();
        Node rootNode = yaml.compose(new InputStreamReader(OtherMain.class.getResourceAsStream("example.raml")));

        if (rootNode == null)
        {
            throw new IllegalArgumentException("rootNode is null");
        }
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        Serializer serializer = new Serializer(new org.yaml.snakeyaml.emitter.Emitter(new OutputStreamWriter(System.out), dumperOptions), new Resolver(),
                dumperOptions, null);
        try
        {
            serializer.open();
            serializer.serialize(rootNode);
            serializer.close();
        }
        catch (IOException e)
        {
            throw new YAMLException(e);
        }
    }
}
