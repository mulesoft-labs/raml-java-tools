package org.raml.ramltopojo;

import java.util.List;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class PluginDef {

    private final String pluginName;
    private final List<String> arguments;
    private final Map<String, String> namedArguments;

    public PluginDef(String pluginName, List<String> arguments) {
        this.pluginName = pluginName;
        this.arguments = arguments;
        this.namedArguments = null;
    }

    public PluginDef(String pluginName, Map<String, String> arguments) {
        this.pluginName = pluginName;
        this.arguments = null;
        this.namedArguments = arguments;
    }


    public String getPluginName() {
        return pluginName;
    }

    public List<String> getArguments() {
        return arguments;
    }


    public Map<String, String> getNamedArguments() {
        return namedArguments;
    }

    @Override
    public String toString() {
        return "PluginDef{" +
                "pluginName='" + pluginName + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
