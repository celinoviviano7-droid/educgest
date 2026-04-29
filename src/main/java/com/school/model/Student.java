package com.school.model;

import java.time.LocalDate;
import java.util.Objects;

public class Student {
    private int id;
    private String matricule;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private String className;
    private String parentName;
    private String parentPhone;
    private String address;
    private LocalDate enrollmentDate;
    private boolean active;

    public Student() {
        this.enrollmentDate = LocalDate.now();
        this.active = true;
    }

    public Student(String matricule, String firstName, String lastName,
                   LocalDate birthDate, String gender, String className) {
        this();
        this.matricule = matricule;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.className = className;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return id == s.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return matricule + " - " + getFullName(); }
}
