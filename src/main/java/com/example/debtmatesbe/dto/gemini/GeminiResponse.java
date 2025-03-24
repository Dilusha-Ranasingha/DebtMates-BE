package com.example.debtmatesbe.dto.gemini;

import lombok.Data;

import java.util.List;

@Data
public class GeminiResponse {
    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;

        @Data
        public static class Content {
            private List<Part> parts;

            @Data
            public static class Part {
                private String text;
            }
        }
    }
}