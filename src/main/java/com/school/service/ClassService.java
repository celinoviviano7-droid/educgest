package com.school.service;

import com.school.model.SchoolClass;
import com.school.repository.ClassRepository;

import java.util.List;
import java.util.Optional;

public class ClassService {

    private final ClassRepository classRepository = new ClassRepository();

    public List<SchoolClass> getAllClasses() {
        return classRepository.findAll();
    }

    public List<String> getAllClassNames() {
        return classRepository.findAllNames();
    }

    public Optional<SchoolClass> findByName(String name) {
        return classRepository.findByName(name);
    }

    public void saveClass(SchoolClass schoolClass) {
        validateClass(schoolClass);
        if (schoolClass.getId() == 0) {
            classRepository.save(schoolClass);
        } else {
            classRepository.update(schoolClass);
        }
    }

    public void deleteClass(int id) {
        classRepository.delete(id);
    }

    private void validateClass(SchoolClass schoolClass) {
        if (schoolClass.getName() == null || schoolClass.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la classe est obligatoire.");
        }
        if (schoolClass.getCapacity() <= 0) {
            throw new IllegalArgumentException("La capacité doit être supérieure à zéro.");
        }
    }
}
