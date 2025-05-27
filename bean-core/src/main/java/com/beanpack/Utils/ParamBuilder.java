package com.beanpack.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ParamBuilder {
    private ObjectNode root;
    private static final ObjectMapper mapper = new ObjectMapper();

    public ParamBuilder() {
        this.root = mapper.createObjectNode();
    }

    public ParamBuilder add(String key, String value) {
        root.put(key, value);
        return this;
    }

    public ParamBuilder add(String key, int value) {
        root.put(key, value);
        return this;
    }

    public ParamBuilder add(String key, boolean value) {
        root.put(key, value);
        return this;
    }

    public ParamBuilder add(String key, double value) {
        root.put(key, value);
        return this;
    }

    // Add more overloads as needed (e.g., for arrays or nested objects)

    public String build() {
        return root.toString();
    }

    public void loadFromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            node.fieldNames().forEachRemaining(key -> {
                JsonNode value = node.get(key);
                if (value.isTextual()) this.add(key, value.asText());
                else if (value.isInt()) this.add(key, value.asInt());
                else if (value.isBoolean()) this.add(key, value.asBoolean());
                else this.add(key, value.toString());
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load params from JSON", e);
        }
    }
}
