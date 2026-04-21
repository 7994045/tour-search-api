package com.toursearch.model;

public class TourResponse {
    private String hotelName;
    private String country;
    private String resort;
    private int stars;
    private int nights;
    private int adults;
    private String meal;
    private int price;

    public TourResponse() {}

    public TourResponse(String hotelName, String country, String resort, int stars, int nights, int adults, String meal, int price) {
        this.hotelName = hotelName;
        this.country = country;
        this.resort = resort;
        this.stars = stars;
        this.nights = nights;
        this.adults = adults;
        this.meal = meal;
        this.price = price;
    }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getResort() { return resort; }
    public void setResort(String resort) { this.resort = resort; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }
    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }
    public String getMeal() { return meal; }
    public void setMeal(String meal) { this.meal = meal; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}