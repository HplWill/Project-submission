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

        System.out.println("Please enter your username:");
        String username = Start.sc.nextLine();
        System.out.println("Please enter password:");
        String pwd = Start.sc.nextLine();
        System.out.println("Please enter your confirmation password:");
        String rePwd = Start.sc.nextLine();
        System.out.println("Please enter a nickname:");
        String nickname = Start.sc.nextLine();
        System.out.println("Please enter your mobile number:");
        String phone = Start.sc.nextLine();
        System.out.println("Please enter the address:");
        String address = Start.sc.nextLine();

        System.out.println();
        Thread.sleep(1500);

        if (MyUtil.isEmpty(username)) {
            System.out.println("The user name cannot be empty！");
            return false;
        }
        if (Objects.isNull(pwd)) {
            System.out.println("The password cannot be empty！");
            return false;
        }
        if (!pwd.equals(rePwd)) {
            System.out.println("The two password entries are inconsistent！");
            return false;
        }
        if (!pwd.matches(RegexConstants.PWD_REGEX) || !rePwd.matches(RegexConstants.PWD_REGEX)) {
            System.out.println("Password format error！");
            return false;
        }
        if (!phone.matches("[0-9]{11}")) {
                System.out.println("The mobile phone number format is incorrect！");
            return false;
        }
        User user = new User(null, username, pwd, nickname, phone, address, null);
        File file = new File(FileConstants.USER_INFO_PATH);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.out.println("The user information file fails to be created because the system is abnormal！");
                return true;
            }
        }

        StringBuilder userList = MyUtil.readFile(FileConstants.USER_INFO_PATH);
        List<User> users = JSONArray.parseArray(userList.toString(), User.class);
        if (CollectionUtils.isEmpty(users)) {
            users = new ArrayList<>();
        } else if (users.stream().map(User::getUsername).collect(Collectors.toList()).contains(user.getUsername())) {
            System.out.println("The user name already exists！");
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
        System.out.println("Registered successfully！");
        return true;
    }
}
