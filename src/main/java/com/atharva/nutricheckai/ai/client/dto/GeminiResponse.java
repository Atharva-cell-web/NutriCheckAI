package com.atharva.nutricheckai.ai.client.dto;

import lombok.Data;
import java.util.List;

@Data
public class GeminiResponse {

    private List<Candidate> candidates;

}