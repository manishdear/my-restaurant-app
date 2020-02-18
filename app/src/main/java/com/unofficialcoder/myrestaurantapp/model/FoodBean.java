package com.unofficialcoder.myrestaurantapp.model;

public class FoodBean {
    private String id,
            name,
            description,
            image,
            price,
            isSize,
            isAddon,
            discount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIsSize() {
        return isSize;
    }

    public void setIsSize(String isSize) {
        this.isSize = isSize;
    }

    public String getIsAddon() {
        return isAddon;
    }

    public void setIsAddon(String isAddon) {
        this.isAddon = isAddon;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
