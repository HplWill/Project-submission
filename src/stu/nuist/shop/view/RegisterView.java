package stu.nuist.shop.view;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
//import javafx.beans.binding.Bindings;
import stu.nuist.shop.Start;
import stu.nuist.shop.constant.FileConstants;
import stu.nuist.shop.constant.RegexConstants;
import stu.nuist.shop.constant.StatusConstants;
import stu.nuist.shop.constant.TableConstants;
import stu.nuist.shop.enity.User;
import stu.nuist.shop.util.MyUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;


public class RegisterView {

    public static boolean showMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t  *REGISTER MODE*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);

        System.out.println("请输入用户名:");
        String username = Start.sc.nextLine();
        System.out.println("请输入密码:");
        String pwd = Start.sc.nextLine();
        System.out.println("请输入确认密码:");
        String rePwd = Start.sc.nextLine();
        System.out.println("请输入昵称:");
        String nickname = Start.sc.nextLine();
        System.out.println("请输入手机号码:");
        String phone = Start.sc.nextLine();
        System.out.println("请输入地址:");
        String address = Start.sc.nextLine();

        System.out.println();
        Thread.sleep(1500);

        if (MyUtil.isEmpty(username)) {
            System.out.println("用户名不能为空！");
            return false;
        }
        if (Objects.isNull(pwd)) {
            System.out.println("密码不能为空！");
            return false;
        }
        if (!pwd.equals(rePwd)) {
            System.out.println("两次密码输入不一致！");
            return false;
        }
        if (!pwd.matches(RegexConstants.PWD_REGEX) || !rePwd.matches(RegexConstants.PWD_REGEX)) {
            System.out.println("密码格式错误！");
            return false;
        }
        if (!phone.matches("[0-9]{11}")) {
            System.out.println("手机号码格式错误！");
            return false;
        }
        User user = new User(null, username, pwd, nickname, phone, address, null);
        File file = new File(FileConstants.USER_INFO_PATH);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.out.println("系统异常，用户信息文件创建失败！");
                return true;
            }
        }

        StringBuilder userList = MyUtil.readFile(FileConstants.USER_INFO_PATH);
        List<User> users = JSONArray.parseArray(userList.toString(), User.class);
        if (CollectionUtils.isEmpty(users)) {
            users = new ArrayList<>();
        } else if (users.stream().map(User::getUsername).collect(Collectors.toList()).contains(user.getUsername())) {
            System.out.println("该用户名已存在！");
            return false;
        }
        List<Long> ids = users.stream().map(User::getId).collect(Collectors.toList());
        Random r = new Random();
        //随机生成用户id
        if (CollectionUtils.isEmpty(ids)) {
            user.setId(Long.parseLong(DateUtil.format(DateTime.now(),"yyyyMMddHHmmssSSS")+ r.nextInt(999)));
        } else {
            long id = Long.parseLong(DateUtil.format(DateTime.now(),"yyyMMddHHmmssSSS") + r.nextInt(999));
            while (ids.contains(id)) {
                id = Long.parseLong(DateUtil.format(DateTime.now(),"yyyMMddHHmmssSSS") + r.nextInt(999));
            }
            user.setId(id);
        }
        user.setStatus(StatusConstants.ENABLE);
        users.add(user);
        MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
        System.out.println("注册成功！");
        return true;
    }
}
