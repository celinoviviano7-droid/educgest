package com.school.service;

import com.school.model.Student;
import com.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

public class StudentService {

    private final StudentRepository studentRepository = new StudentRepository();

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClass(className);
    }

    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentRepository.search(query.trim());
    }

    public Optional<Student> findById(int id) {
        return studentRepository.findById(id);
    }

    public void saveStudent(Student student) {
        validateStudent(student);

        if (student.getMatricule() == null || student.getMatricule().isEmpty()) {
            student.setMatricule(studentRepository.generateMatricule());
        } else {
            Optional<Student> existing = studentRepository.findByMatricule(student.getMatricule());
            if (existing.isPresent() && existing.get().getId() != student.getId()) {
                throw new IllegalArgumentException("Ce matricule est déjà utilisé.");
            }
        }

        if (student.getId() == 0) {
            studentRepository.save(student);
        } else {
            studentRepository.update(student);
        }
    }

    public void deleteStudent(int id) {
        studentRepository.deactivate(id);
    }

    public int getTotalStudentsCount() {
        return studentRepository.countAll();
    }

    public int getStudentsCountByClass(String className) {
        return studentRepository.countByClass(className);
    }

    public String generateMatricule() {
        return studentRepository.generateMatricule();
    }

    private void validateStudent(Student student) {
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire.");
        }
        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire.");
        }
        if (student.getClassName() == null || student.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("La classe est obligatoire.");
        }
    }
}
