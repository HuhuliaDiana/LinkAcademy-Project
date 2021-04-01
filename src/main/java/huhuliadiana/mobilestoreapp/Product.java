package huhuliadiana.mobilestoreapp;

import javax.persistence.*;
import java.text.DecimalFormat;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String brand;
    private String model;
    private String memory_RAM;
    private String diagonal;
    private String internet_speed;
    private String main_camera;
    private String battery_capacity;
    private float price;

    private String photo;
    private int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMemory_RAM() {
        return memory_RAM;
    }

    public void setMemory_RAM(String memory_RAM) {
        this.memory_RAM = memory_RAM;
    }

    public String getDiagonal() {
        return diagonal;
    }

    public void setDiagonal(String diagonal) {
        this.diagonal = diagonal;
    }

    public String getInternet_speed() {
        return internet_speed;
    }

    public void setInternet_speed(String internet_speed) {
        this.internet_speed = internet_speed;
    }

    public String getMain_camera() {
        return main_camera;
    }

    public void setMain_camera(String main_camera) {
        this.main_camera = main_camera;
    }

    public String getBattery_capacity() {
        return battery_capacity;
    }

    public void setBattery_capacity(String battery_capacity) {
        this.battery_capacity = battery_capacity;
    }

    public float getPrice() {
        return price;
    }


    public void setPrice(float price) {
        this.price =price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
