package com.se441.textbooklibrary.dto;

public class TextbookRequest {
    
    private Integer id;

    public TextbookRequest() {
    }

    public TextbookRequest(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
