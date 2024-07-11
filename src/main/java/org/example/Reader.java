package org.example;

import java.util.ArrayList;

public class Reader {
    private int id;
    private String name;
//    private List<Book> books;

    public Reader(int id, String name){
        this.id = id;
        this.name = name;
//        this.books = new ArrayList<>();
    }

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }
}

