package com.TrainingTracker.TraingingTracker.Util;

import tools.jackson.databind.JsonNode;

public final class CfUtil {

    private CfUtil() {
    }

    public static String text(JsonNode node) {
        return node == null || node.isNull() ? null : node.asText();
    }

    public static Long optionalLong(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asLong();
    }

    public static String optionalText(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asText();
    }
}
