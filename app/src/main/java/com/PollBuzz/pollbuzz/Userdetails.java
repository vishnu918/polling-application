package com.PollBuzz.pollbuzz;

import java.util.PriorityQueue;

public class Userdetails {
    private String Name;
    private String Username;
    private int age;

    public Userdetails() {
    }

    public Userdetails(String name, String username, int age) {
        Name = name;
        Username = username;
        this.age = age;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
