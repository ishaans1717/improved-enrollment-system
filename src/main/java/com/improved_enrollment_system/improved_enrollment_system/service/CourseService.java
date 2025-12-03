package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.entity.Catalog;
import com.improved_enrollment_system.improved_enrollment_system.entity.Course;
import com.improved_enrollment_system.improved_enrollment_system.repository.CatalogRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CatalogRepository catalogRepository;

    public CourseService(CourseRepository courseRepository, CatalogRepository catalogRepository) {
        this.courseRepository = courseRepository;
        this.catalogRepository = catalogRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public List<Course> getCoursesForCatalog(Long catalogId) {
        return courseRepository.findByCatalogId(catalogId);
    }

    public Course createCourse(Long catalogId, Course course) {
        Catalog catalog = catalogRepository.findById(catalogId)
                .orElseThrow(() -> new RuntimeException("Catalog not found"));
        catalog.addCourse(course);
        return courseRepository.save(course);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}