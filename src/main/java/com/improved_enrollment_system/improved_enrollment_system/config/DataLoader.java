package com.improved_enrollment_system.improved_enrollment_system.config;

import com.improved_enrollment_system.improved_enrollment_system.entity.Course;
import com.improved_enrollment_system.improved_enrollment_system.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadDefaultCourses(CourseRepository courseRepository) {
        return args -> {
            if (courseRepository.count() > 0) {
                System.out.println("✅ Courses already exist in database (" + courseRepository.count() + " courses)");
                return;
            }

            System.out.println("📚 Loading default IT courses...");

            courseRepository.save(createCourse("IT", "168", "Structured Problem Solving Using The Computer", 4,
                    "Introduction to problem solving and programming fundamentals using computers", 30));

            courseRepository.save(createCourse("IT", "170", "Scripting Languages and Automation", 3,
                    "Learn scripting languages for automation tasks and workflow optimization", 30));

            courseRepository.save(createCourse("IT", "178", "Computer Application Programming", 3,
                    "Fundamental concepts of computer application programming and software development", 30));

            courseRepository.save(createCourse("IT", "179", "Introduction To Data Structures", 3,
                    "Basic data structures including arrays, lists, stacks, queues and their applications", 30));

            courseRepository.save(createCourse("IT", "180", "C++ Programming", 1,
                    "Introduction to C++ programming language syntax and object-oriented concepts", 25));

            courseRepository.save(createCourse("IT", "191", "Introduction To IT Professional Practice", 1,
                    "Professional practices, ethics, and career development in the IT field", 25));

            courseRepository.save(createCourse("IT", "214", "Social, Legal, And Ethical Issues In Information Technology", 3,
                    "Ethics, legal considerations, and social impact of information technology", 30));

            courseRepository.save(createCourse("IT", "215", "Introduction to Applied Artificial Intelligence", 3,
                    "Fundamentals of AI, machine learning algorithms and practical applications", 30));

            courseRepository.save(createCourse("IT", "225", "Computer Organization", 3,
                    "Architecture and organization of computer systems and hardware components", 30));

            courseRepository.save(createCourse("IT", "244", "Business Analytics for Artificial Intelligence", 3,
                    "Apply AI and machine learning techniques to business analytics and decision making", 30));

            courseRepository.save(createCourse("IT", "250", "Fundamentals of Information Assurance and Security", 3,
                    "Security principles, risk management, and cybersecurity best practices", 30));

            courseRepository.save(createCourse("IT", "254", "Hardware And Software Concepts", 3,
                    "Understanding hardware and software interactions, system architecture and integration", 30));

            courseRepository.save(createCourse("IT", "261", "Systems Development I", 3,
                    "Software development lifecycle, methodologies, and project planning techniques", 30));

            courseRepository.save(createCourse("IT", "262", "Information Technology Project Management", 3,
                    "Project management principles, tools, and methodologies for IT projects", 30));

            courseRepository.save(createCourse("IT", "272", "Cobol As A Second Language", 4,
                    "COBOL programming for business applications and legacy system maintenance", 25));

            courseRepository.save(createCourse("IT", "275", "Java As A Second Language", 4,
                    "Java programming fundamentals, object-oriented design, and application development", 30));

            courseRepository.save(createCourse("IT", "276", "Data Communications", 3,
                    "Principles of data transmission, networking protocols, and communication systems", 30));

            courseRepository.save(createCourse("IT", "279", "Algorithms And Data Structures", 3,
                    "Advanced algorithms, complexity analysis, and efficient data structure implementations", 30));

            System.out.println("✅ Successfully loaded 18 IT courses!");
        };
    }

    private Course createCourse(String subject, String code, String title, int credits, String description, int capacity) {
        Course course = new Course();
        course.setSubject(subject);
        course.setCode(code);
        course.setTitle(title);
        course.setCredits(credits);
        course.setDescription(description);
        course.setCapacity(capacity);
        return course;
    }
}