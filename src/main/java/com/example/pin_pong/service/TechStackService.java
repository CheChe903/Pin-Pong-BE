package com.example.pin_pong.service;

import com.example.pin_pong.domain.TechStack;
import com.example.pin_pong.repository.TechStackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TechStackService {

    private final TechStackRepository techStackRepository;

    @Autowired
    public TechStackService(TechStackRepository techStackRepository) {
        this.techStackRepository = techStackRepository;
    }

    public Optional<TechStack> findByName(String techName) {
        return techStackRepository.findByTechName(techName);
    }
}
