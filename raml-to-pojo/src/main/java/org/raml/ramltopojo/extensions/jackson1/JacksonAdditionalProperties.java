package org.raml.ramltopojo.extensions.jackson1;


import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.raml.ramltopojo.extensions.GenericJacksonAdditionalProperties;

/**
 * Created. There, you have it.
 */
public class JacksonAdditionalProperties extends GenericJacksonAdditionalProperties {

    public JacksonAdditionalProperties(){
        super(JsonAnyGetter.class, JsonAnySetter.class, JsonIgnore.class);
    }
}
