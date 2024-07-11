package org.example;

public class Book {
    private String bookName;
    private String author;
    private String readStatus;
    private String bookFormat;

    public Book(String title, String author, String readStatus, String bookFormat){
        this.bookName = title;
        this.author = author;
        this.readStatus = readStatus;
        this.bookFormat = bookFormat;
    }

    public String getName(){
        return bookName;
    }

    public String getAuthor(){
        return author;
    }

    public String getReadStatus(){
        return readStatus;
    }


}
