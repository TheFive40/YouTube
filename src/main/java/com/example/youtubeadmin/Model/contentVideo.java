package com.example.youtubeadmin.Model;

public class contentVideo {
    private String dimension, definition, caption;
    private boolean licensed;


    public contentVideo(String dimension, String definition, String caption, boolean licensed) {
        this.dimension = dimension;
        this.definition = definition;
        this.caption = caption;
        this.licensed = licensed;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setLicensed(boolean licensed) {
        this.licensed = licensed;
    }

    public String getDimension() {
        return dimension;
    }

    public String getDefinition() {
        return definition;
    }

    public String getCaption() {
        return caption;
    }

    public boolean isLicensed() {
        return licensed;
    }
}
