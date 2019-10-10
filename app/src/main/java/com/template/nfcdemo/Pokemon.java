package com.template.nfcdemo;

import java.io.Serializable;

public class Pokemon implements Serializable {

    String name;
    String height;
    String weight;
    String ability;
    String image;

    public Pokemon(String name, String height, String weight, String ability, String image) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.ability = ability;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "name='" + name + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", ability='" + ability + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
