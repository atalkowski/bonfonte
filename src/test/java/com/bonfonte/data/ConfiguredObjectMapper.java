package com.bonfonte.data;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Configures the ObjectMapper used by the Jackson JSON library.
 */
public class ConfiguredObjectMapper extends ObjectMapper {
    public ConfiguredObjectMapper() {
        SerializationConfig serializationConfig = this.getSerializationConfig();

        assert serializationConfig != null;

        serializationConfig.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        DeserializationConfig deserializationConfig = this.getDeserializationConfig();

        assert deserializationConfig != null;
    }
}
