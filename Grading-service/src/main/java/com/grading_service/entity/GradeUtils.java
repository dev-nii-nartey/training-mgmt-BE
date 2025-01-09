package com.grading_service.entity;

public class GradeUtils {

    public static String assignLetterGrade(double score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 75) return "B+";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        if (score >= 40) return "E";
        return "F";
    }
}
