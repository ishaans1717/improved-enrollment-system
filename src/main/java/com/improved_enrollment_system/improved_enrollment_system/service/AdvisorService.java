package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.entity.Advisor;
import com.improved_enrollment_system.improved_enrollment_system.repository.AdvisorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvisorService {

    private final AdvisorRepository advisorRepository;

    public AdvisorService(AdvisorRepository advisorRepository) {
        this.advisorRepository = advisorRepository;
    }


    public List<Advisor> getAllAdvisors() {

        return advisorRepository.findAll();
    }

    public Advisor getAdvisorById(Long id) {
        return advisorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advisor not found"));
    }

    public Advisor saveAdvisor(Advisor advisor) {

        return advisorRepository.save(advisor);
    }

    public Boolean deleteAdvisor(Long id) {
        if (advisorRepository.existsById(id)) {
            advisorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
