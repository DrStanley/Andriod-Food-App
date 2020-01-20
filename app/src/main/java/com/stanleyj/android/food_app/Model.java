package com.stanleyj.android.food_app;

/**
 * Created by Stanley on 2019/01/12.
 */
public class Model {
    String title;
    String image1;
    String image2;
    String image3;
    String description;
    String food_status;
    String price;
    String foodID;

    String name, phone_number, email, image, usd;

    public Model(String image, String name, String num, String ema, String usd) {
        this.image = image;
        this.name = name;
        this.phone_number = num;
        this.email = ema;
        this.usd = usd;
    }

    public Model() {
    }



    public Model(String title, String image1, String image2, String image3, String description, String status, String price, String foodID) {
        this.title = title;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.description = description;
        this.food_status = status;
        this.price = price;
        this.foodID = foodID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage1() {
        return image1;
    }
    public String getFood_status() {
        return food_status;
    }

    public void setFood_status(String food_status) {
        this.food_status = food_status;
    }
    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
