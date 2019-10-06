package com.example.myapplication;

public class AssignmentSql {

    long id;
    String assignmentName;

    // constructors
    public AssignmentSql() {

    }

    public AssignmentSql(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public AssignmentSql(int id, String assignmentName) {
        this.id = id;
        this.assignmentName = assignmentName;
    }


    // setter
    public void setId(long id) {
        this.id = id;
    }

    public void setAssignmentName(String tag_name) {
        this.assignmentName = tag_name;
    }

    // getter
    public long getId() {
        return this.id;
    }

    public String getAssignmentName() {
        return this.assignmentName;
    }
}