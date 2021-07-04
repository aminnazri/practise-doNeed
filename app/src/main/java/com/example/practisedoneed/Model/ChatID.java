package com.example.practisedoneed.Model;

public class ChatID {

    private String id;
    private String member1;
    private String member2;

    public ChatID(String id, String member1, String member2) {
        this.id = id;
        this.member1 = member1;
        this.member2 = member2;
    }

    public ChatID(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMember1() {
        return member1;
    }

    public void setMember1(String member1) {
        this.member1 = member1;
    }

    public String getMember2() {
        return member2;
    }

    public void setMember2(String member2) {
        this.member2 = member2;
    }
}
