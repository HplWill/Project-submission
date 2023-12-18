package stu.nuist.shop.enity;

import stu.nuist.shop.constant.StringConstants;

import java.util.Objects;

public class User {
    //id,username,password,nickname,phone,address,status
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String address;
    private Integer status;

    public User() {
    }

    public User(Long id, String username, String password, String nickname,
                String phone, String address, Integer status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("用户信息{");
        if (Objects.nonNull(id)) {
            sb.append("用户编号=").append(id).append(StringConstants.STRING_COMMA);
        }
        if (Objects.nonNull(username)) {
            sb.append("用户名=")
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(username)
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(StringConstants.STRING_COMMA);
        }
        if (Objects.nonNull(password)) {
            sb.append("用户密码=")
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(password)
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(StringConstants.STRING_COMMA);
        }
        if (Objects.nonNull(nickname)) {
            sb.append("用户昵称=")
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(nickname)
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(StringConstants.STRING_COMMA);
        }
        if (Objects.nonNull(phone)) {
            sb.append("手机号=")
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(phone)
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(StringConstants.STRING_COMMA);
        }
        if (Objects.nonNull(address)) {
            sb.append("地址=")
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(address)
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(StringConstants.STRING_COMMA);
        }
        if (Objects.nonNull(status)) {
            sb.append("用户状态=")
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(status == 1 ? StringConstants.STATUS_ENABLE : StringConstants.STATUS_DISABLE)
                    .append(StringConstants.STRING_SINGLE_QUOTES)
                    .append(StringConstants.STRING_COMMA);
        }
        sb.deleteCharAt(sb.lastIndexOf(StringConstants.STRING_COMMA));
        sb.append("}");
        return sb.toString();
    }
}
