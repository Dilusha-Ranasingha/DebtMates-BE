package com.example.debtmatesbe.dto.gemini;

import lombok.Data;

import java.util.List;

@Data
public class GeminiRequest {
    private List<Content> contents;
    private SystemInstruction systemInstruction;

    @Data
    public static class Content {
        private List<Part> parts;

        @Data
        public static class Part {
            private String text;
        }
    }

    @Data
    public static class SystemInstruction {
        private List<Part> parts;

        @Data
        public static class Part {
            private String text;
        }
    }
}