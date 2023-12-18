package stu.nuist.shop.constant;

public class RegexConstants {
    /**
     * 密码规则【包含英文数字，长度大于等于8】
     */
    public static final String PWD_REGEX = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}$";
}
