package stu.nuist.shop.enity;

public class Product {
    // id,name,price
    private Long id;
    private String name;
    private Double price;
    private String unit;

    public Product() {
    }

    public Product(Long id, String name, Double price, String unit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "product [ID=" + id + ", Name=" + name + ", price=" + price + ", unit=" + unit
                + "]";
    }

}
