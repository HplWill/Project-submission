package stu.nuist.shop.enity;

import java.util.Date;
import java.util.List;

public class Order {
    //id,user_id,no,num,total_price,create_date
    private Long id;
    private Long userId;
    private String no;
    private Integer num;
    private Double totalPrice;
    private Date createDate;

    public Order() {
    }

    public Order(Long id, Long userId, String no, Integer num,
                 Double totalPrice, Date createDate) {
        this.id = id;
        this.userId = userId;
        this.no = no;
        this.num = num;
        this.totalPrice = totalPrice;
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "订单信息{" +
                "订单编号=" + id +
                ", 用户编号=" + userId +
                ", 订单编码='" + no + '\'' +
                ", 订单商品总数=" + num +
                ", 订单总价=" + totalPrice +
                ", 创建时间=" + createDate +
                '}';
    }
}
