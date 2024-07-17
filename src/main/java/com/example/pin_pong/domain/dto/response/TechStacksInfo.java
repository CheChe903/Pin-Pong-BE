package com.example.pin_pong.domain.dto.response;


import com.example.pin_pong.domain.TechStack;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TechStacksInfo {
    private List<String> techStacks;
}