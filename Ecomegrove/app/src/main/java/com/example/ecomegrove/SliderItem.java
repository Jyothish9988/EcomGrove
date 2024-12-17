package com.example.ecomegrove;

public class SliderItem {
    private String imageUrl;
    private String text;

    public SliderItem(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }
}
