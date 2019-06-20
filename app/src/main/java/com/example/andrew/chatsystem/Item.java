package com.example.andrew.chatsystem;

public class Item {

    private String name;


    public Item(String name) {
        this.name = name;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        Item itemCompare = (Item) obj;
        if(itemCompare.getName().equals(this.getName()))
            return true;

        return false;
    }
}