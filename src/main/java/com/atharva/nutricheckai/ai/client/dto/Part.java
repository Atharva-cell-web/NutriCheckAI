package com.atharva.nutricheckai.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Part {

    private String text;

    // For image/vision requests (inline_data)
    private InlineData inline_data;

    // Convenience constructor for text-only parts
    public Part(String text) {
        this.text = text;
        this.inline_data = null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InlineData {
        private String mime_type;
        private String data; // base64-encoded image bytes
    }
}