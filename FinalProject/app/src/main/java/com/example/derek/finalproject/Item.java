package com.example.derek.finalproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Derek on 4/24/16.
 */
@JsonIgnoreProperties
public class Item implements Serializable{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String owner;
    private String priceRange;
    private String tag;
    private String releaseDate;
    private String state;
    private String description;
    private String image;
    private boolean favored = false;



    private int position;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public String getKeyInFavor() {
        return keyInFavor;
    }

    public void setKeyInFavor(String keyInFavor) {
        this.keyInFavor = keyInFavor;
    }

    private String keyInFavor;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;


    public boolean isFavored() {
        return favored;
    }

    public void setFavored(boolean favored) {
        this.favored = favored;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
    Item(String name,String owner,String priceRange,String tag,String releaseDate,String state,String description,String image)
    {
        this.name=name;
        this.owner=owner;
        this.priceRange=priceRange;
        this.tag=tag;
        this.releaseDate=releaseDate;
        this.state=state;
        this.description=description;
        this.image=image;
    }


    Item(){};

}
