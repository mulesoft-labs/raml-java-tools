package org.raml.ramltopojo.extensions.jackson2;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.raml.ramltopojo.extensions.GenericJacksonAdditionalProperties;

/**
 * Created. There, you have it.
 */
public class JacksonAdditionalProperties extends GenericJacksonAdditionalProperties {

    public JacksonAdditionalProperties(){
        super(JsonAnyGetter.class, JsonAnySetter.class, JsonIgnore.class);
    }
}
