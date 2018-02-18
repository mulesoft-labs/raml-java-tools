package org.raml.ramltopojo;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class PluginDef {

    private final String pluginName;
    private final List<String> arguments;

    public PluginDef(String pluginName, List<String> arguments) {
        this.pluginName = pluginName;
        this.arguments = arguments;
    }

    public String getPluginName() {
        return pluginName;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "PluginDef{" +
                "pluginName='" + pluginName + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
