package stu.nuist.shop.enity;

public class Detail {
    //id,product_id,num,total_price,order_id
    private Long id;
    private Long productId;
    private Integer num;
    private Double totalPrice;
    private Long orderId;

    public Detail() {
    }

    public Detail(Long id, Long productId, Integer num,
                  Double totalPrice, Long orderId) {
        this.id = id;
        this.productId = productId;
        this.num = num;
        this.totalPrice = totalPrice;
        this.orderId = orderId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "预购商品信息{" +
                "编号=" + id +
                ", 商品编号=" + productId +
                ", 数量=" + num +
                ", 总价=" + totalPrice +
                '}';
    }
}
