package com.atharva.nutricheckai.ai.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContentResponse {

    private List<PartResponse> parts;

}