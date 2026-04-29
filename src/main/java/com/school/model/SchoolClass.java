package com.school.model;

import java.util.Objects;

public class SchoolClass {
    private int id;
    private String name;
    private String level;
    private String teacherName;
    private int capacity;
    private int currentCount;

    public SchoolClass() {}

    public SchoolClass(String name, String level, String teacherName, int capacity) {
        this.name = name;
        this.level = level;
        this.teacherName = teacherName;
        this.capacity = capacity;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getCurrentCount() { return currentCount; }
    public void setCurrentCount(int currentCount) { this.currentCount = currentCount; }

    public int getAvailableSpots() { return capacity - currentCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolClass)) return false;
        SchoolClass sc = (SchoolClass) o;
        return id == sc.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return name + " (" + level + ")"; }
}
