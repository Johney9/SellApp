package com.app.sell.model;

public class Offer {

    private String id;
    private String title;
    private Double price;
    private String description;
    private String location;
    private String image;
    private String offererId;
    private Long timestamp;

    public Offer() {
    }

    public Offer(String id, String title, Double price, String description, String location, String image, String offererId, Long timestamp) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.location = location;
        this.image = image;
        this.offererId = offererId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOffererId() {
        return offererId;
    }

    public void setOffererId(String offererId) {
        this.offererId = offererId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
