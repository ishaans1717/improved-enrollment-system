package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.entity.Administrator;
import com.improved_enrollment_system.improved_enrollment_system.repository.AdministratorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    public AdministratorService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    public List<Administrator> getAllAdministrators() {
        return administratorRepository.findAll();
    }

    public Administrator getAdministratorById(Long id) {
        return administratorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrator not found"));
    }

    public Administrator saveAdministrator(Administrator administrator) {
        return administratorRepository.save(administrator);
    }

    public boolean deleteAdministrator(Long id) {
        if (administratorRepository.existsById(id)) {
            administratorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
