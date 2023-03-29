package com.example.todolist;

import java.util.Date;

public class Task {


    private String taskName;
    private String date;
    private boolean complete;

    public Task(String taskName, String date, Boolean complete) {
        this.taskName = taskName;
        this.date = date;
        this.complete = complete;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDate() {
        return date;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
