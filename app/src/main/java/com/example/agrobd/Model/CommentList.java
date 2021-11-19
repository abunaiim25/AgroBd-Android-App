package com.example.agrobd.Model;

public class CommentList {

    String comment,publisher,date;

    public CommentList(String comment, String publisher, String date) {
        this.comment = comment;
        this.publisher = publisher;
        this.date = date;
    }

    public CommentList() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
