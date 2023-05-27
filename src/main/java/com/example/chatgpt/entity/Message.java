package com.example.chatgpt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    @JsonProperty("content")
    private String content;

    @JsonProperty("role")
    private String role;
}
