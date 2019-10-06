package com.example.myapplication;

public class CoursesSql {

    int id;
    String courseName;
    int status;
    String created_at;

    // constructors
    public CoursesSql() {
    }

    public CoursesSql(String courseName, int status) {
        this.courseName = courseName;
        this.status = status;
    }

    public CoursesSql(int id, String courseName, int status) {
        this.id = id;
        this.courseName = courseName;
        this.status = status;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreatedAt(String created_at){
        this.created_at = created_at;
    }

    // getters
    public long getId() {
        return this.id;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public int getStatus() {
        return this.status;
    }
}
