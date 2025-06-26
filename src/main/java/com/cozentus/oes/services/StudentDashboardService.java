package com.cozentus.oes.services;

import com.cozentus.oes.dto.StudentDashboardDTO;

public interface StudentDashboardService {
    StudentDashboardDTO getDashboardForStudent(Integer studentId);
    Integer getCurrentStudentId(); // Implement via security context
}
