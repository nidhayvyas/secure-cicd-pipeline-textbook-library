package com.se441.textbooklibrary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "textbooks")
public class Textbook {

    @Id
    private String _id;
    
    private String name;
    private Integer id;
    private String description;
    private String image;
    private String author;
    private String edition;

    // Default constructor
    public Textbook() {
    }

    // Constructor with fields
    public Textbook(String name, Integer id, String description, String image, String author, String edition) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.image = image;
        this.author = author;
        this.edition = edition;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }
}
