package com.toursearch.dto;

public class Tour {
    private String hotelName;
    private Double rating;
    private String country;
    private String city;
    private String hotelImage;
    private Double price;
    private String bookUrl;
    
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getHotelImage() { return hotelImage; }
    public void setHotelImage(String hotelImage) { this.hotelImage = hotelImage; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getBookUrl() { return bookUrl; }
    public void setBookUrl(String bookUrl) { this.bookUrl = bookUrl; }
}