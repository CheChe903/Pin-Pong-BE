package com.example.pin_pong.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateTechStacksRequest {
    private Set<String> techStackNames;
}
