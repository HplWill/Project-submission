package stu.nuist.shop.view;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import stu.nuist.shop.Start;
import stu.nuist.shop.constant.*;
import stu.nuist.shop.enity.Product;
import stu.nuist.shop.enity.User;
import stu.nuist.shop.util.MyUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Collectors;

public class ManagerView {

    public static boolean showLoginMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t *LOGIN MANAGER*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.println("请输入用户名:");
        String username = Start.sc.nextLine();
        System.out.println("请输入密码:");
        String pwd = Start.sc.nextLine();

        File file = new File(FileConstants.MANAGER_INFO_PATH);
        if (file.exists()) {
            StringBuilder userList = MyUtil.readFile(FileConstants.MANAGER_INFO_PATH);
            List<User> users = JSONArray.parseArray(userList.toString(), User.class);
            if (CollectionUtils.isEmpty(users) || !users.stream()
                    .map(User::getUsername).collect(Collectors.toList()).contains(username)) {
                System.out.println("此管理员用户名不存在！");
                return false;
            } else if (users.stream().noneMatch(user ->
                    user.getUsername().equals(username) && user.getPassword().equals(pwd))) {
                System.out.println("该管理员用户输入的密码错误！");
                return false;
            }
            User userLog = users.stream().filter(user ->
                            user.getUsername().equals(username) && user.getPassword().equals(pwd))
                    .collect(Collectors.toList()).get(0);
            if (StatusConstants.ENABLE == userLog.getStatus()) {
                Start.loginManager = userLog;
            } else {
                System.out.println("管理员用户状态为" + StringConstants.STATUS_DISABLE + ",无法登录！");
                Thread.sleep(3000);
            }
        } else {
            System.out.println("系统异常，用户信息文件错误！");
            return true;
        }
        System.out.println("欢迎管理员【" + Start.loginManager.getNickname() + "】登录！");
        Thread.sleep(3000);
        //showManagerMenu();
        return true;
    }

    public static void showSuperManagerMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t  *SUPER MANAGER*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM_RIGHT + "————————————————————————" + TableConstants.TOP_BOTTOM_LEFT);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.用户查看\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.管理员查看\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t3.添加管理员\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.刷新\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.退出登录\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.print("请选择一个功能：");
        superManagerFunctionSwitch();
    }

    private static void superManagerFunctionSwitch() throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                showUserInfoList();
                break;
            case "2":
                showManagerInfoList();
                break;
            case "3":
                boolean isRegisterSuccess = addMangerInfo();
                while (!isRegisterSuccess) {
                    Thread.sleep(3000);
                    isRegisterSuccess = addMangerInfo();
                }
                superManagerFunctionSwitch();
                break;
            case "9":
                showSuperManagerMenu();
                break;
            case "0":
                Start.showMainMenu();
                break;
            default:
                System.out.println("无此功能，敬请期待！");
                Thread.sleep(3000);
                showSuperManagerMenu();

                break;
        }
    }

    private static void showUserInfoList() throws Exception {
        File file = new File(FileConstants.USER_INFO_PATH);
        if (file.exists()) {
            StringBuilder userList = MyUtil.readFile(FileConstants.USER_INFO_PATH);
            List<User> users = JSONArray.parseArray(userList.toString(), User.class);
            if (CollectionUtils.isEmpty(users)) {
                System.out.println("用户信息列表为空，无法展示！");
                Thread.sleep(3000);
                showSuperManagerMenu();
            } else {
                System.out.println("用户信息列表：");
                for (int i = 0; i < users.size(); i++) {
                    System.out.println((i + 1) + ". " + users.get(i).toString());
                }
                System.out.println();
                System.out.println("是否对指定用户进行操作？(no-返回超管菜单,输入列表编号进行操作选择)：");
                String key = Start.sc.nextLine();
                if ("no".equals(key)) {
                    showSuperManagerMenu();
                } else {
                    operateUserInfo(users, Integer.parseInt(key) - 1);
                }
            }
        } else {
            boolean newFile = file.createNewFile();
            if (newFile) {
                System.out.println("用户信息列表为空，无法展示！");
            } else {
                System.out.println("系统异常，用户信息文件创建失败！");
            }
            Thread.sleep(3000);
            showSuperManagerMenu();
        }
    }

    private static void operateUserInfo(List<User> users, int i) throws Exception {
        MyUtil.clearConsole();
        System.out.println(users.get(i).toString());
        System.out.println();
        System.out.println("1.修改用户状态");
        System.out.println("0.返回");
        System.out.println("请选择你对该用户信息要进行的操作：");
        operateUserInfoSwitch(users, i);
    }

    private static void operateUserInfoSwitch(List<User> users, int i) throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                users.get(i).setStatus(-1 * users.get(i).getStatus());
                MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                System.out.println("用户【" + users.get(i).getId() + "】状态修改成功！");
                Thread.sleep(3000);
                showSuperManagerMenu();
                break;
            case "0":
                showSuperManagerMenu();
                break;
            default:
                System.out.println("无此功能，敬请期待！");
                Thread.sleep(3000);
                operateUserInfo(users, i);
                break;
        }
    }

    private static void showManagerInfoList() throws Exception {
        File file = new File(FileConstants.MANAGER_INFO_PATH);
        if (file.exists()) {
            StringBuilder userList = MyUtil.readFile(FileConstants.MANAGER_INFO_PATH);
            List<User> users = JSONArray.parseArray(userList.toString(), User.class);
            if (CollectionUtils.isEmpty(users)) {
                System.out.println("管理员信息列表为空，无法展示！");
                Thread.sleep(3000);
                showSuperManagerMenu();
            } else {
                System.out.println("管理员信息列表：");
                for (int i = 0; i < users.size(); i++) {
                    System.out.println((i + 1) + ". " + users.get(i).toString());
                }
                System.out.println();
                System.out.println("是否对指定管理员用户进行操作？(no-返回超管菜单,输入列表编号进行操作选择)：");
                String key = Start.sc.nextLine();
                if ("no".equals(key)) {
                    showSuperManagerMenu();
                } else {
                    operateManagerInfo(users, Integer.parseInt(key) - 1);
                }
            }
        } else {
            boolean newFile = file.createNewFile();
            if (newFile) {
                System.out.println("管理员信息列表为空，无法展示！");
            } else {
                System.out.println("系统异常，管理员信息文件创建失败！");
            }
            Thread.sleep(3000);
            showSuperManagerMenu();
        }
    }

    private static void operateManagerInfo(List<User> users, int i) throws Exception {
        if (StringConstants.SUPER_MANAGER_USERNAME.equals(users.get(i).getUsername())) {
            System.out.println("你不能对你这名超管进行操作！");
            Thread.sleep(1500);
            System.out.println("是否对指定管理员用户进行操作？(no-返回超管菜单,输入列表编号进行操作选择)：");
            String key = Start.sc.nextLine();
            if ("no".equals(key)) {
                showSuperManagerMenu();
            } else {
                operateManagerInfo(users, Integer.parseInt(key) - 1);
            }
        } else {
            showOperateManagerInfo(users, i);

        }
    }

    private static void showOperateManagerInfo(List<User> users, int i) throws Exception {
        MyUtil.clearConsole();
        System.out.println(users.get(i).toString());
        System.out.println();
        System.out.println("1.修改管理员状态");
        System.out.println("2.修改管理员密码");
        System.out.println("3.修改管理员昵称");
        System.out.println("0.返回");
        System.out.println("请选择你对该用户信息要进行的操作：");
        operateManagerInfoSwitch(users, i);
    }

    private static void operateManagerInfoSwitch(List<User> users, int i) throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                users.get(i).setStatus(-1 * users.get(i).getStatus());
                MyUtil.writeFile(FileConstants.MANAGER_INFO_PATH, JSON.toJSONString(users));
                System.out.println("管理员【" + users.get(i).getId() + "】状态修改成功！");
                Thread.sleep(3000);
                showSuperManagerMenu();
                break;
            case "2":
                modifyMangerPassword(users, i);
                break;
            case "3":
                modifyMangerNickName(users, i);
                break;
            case "0":
                showSuperManagerMenu();
                break;
            default:
                System.out.println("无此功能，敬请期待！");
                Thread.sleep(3000);
                operateManagerInfo(users, i);
                break;
        }
    }

    private static void modifyMangerPassword(List<User> users, int i) throws Exception {
        System.out.println("请输入你要修改后的密码：");
        String pwd = Start.sc.nextLine();
        if (Objects.isNull(pwd)) {
            System.out.println("密码不能为空！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else if (!pwd.matches(RegexConstants.PWD_REGEX)) {
            System.out.println("密码格式错误！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else if (pwd.equals(users.get(i).getPassword())) {
            System.out.println("密码不能和原密码相同！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else {
            users.get(i).setPassword(pwd);
            MyUtil.writeFile(FileConstants.MANAGER_INFO_PATH, JSON.toJSONString(users));
            System.out.println("管理员【" + users.get(i).getId() + "】密码修改成功！");
            Thread.sleep(3000);
            showSuperManagerMenu();
        }
    }

    private static void modifyMangerNickName(List<User> users, int i) throws Exception {
        System.out.println("请输入你要修改后的昵称：");
        String nickName = Start.sc.nextLine();
        if (Objects.isNull(nickName)) {
            System.out.println("昵称不能为空！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else if (nickName.equals(users.get(i).getNickname())) {
            System.out.println("昵称不能和原昵称相同！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else {
            users.get(i).setNickname(nickName);
            MyUtil.writeFile(FileConstants.MANAGER_INFO_PATH, JSON.toJSONString(users));
            System.out.println("管理员【" + users.get(i).getId() + "】昵称修改成功！");
            Thread.sleep(3000);
            showSuperManagerMenu();
        }
    }

    private static boolean addMangerInfo() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t   *ADD MANAGER*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);

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
        User user = new User(null, null, pwd, nickname, phone, address, null);
        File file = new File(FileConstants.MANAGER_INFO_PATH);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.out.println("系统异常，用户信息文件创建失败！");
                return true;
            }
        }

        StringBuilder userList = MyUtil.readFile(FileConstants.MANAGER_INFO_PATH);
        List<User> users = JSONArray.parseArray(userList.toString(), User.class);
        if (CollectionUtils.isEmpty(users)) {
            users = new ArrayList<>();
        }
        List<Long> ids = users.stream().map(User::getId).collect(Collectors.toList());
        //生管理员id
        if (CollectionUtils.isEmpty(ids)) {
            user.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM) + String.format("%03d", 1)));
        } else {
            List<Long> list = ids.stream()
                    .filter(id -> id.toString().startsWith(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)) {
                user.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM) + String.format("%03d", 1)));
            } else {
                OptionalLong ol = list.stream()
                        .mapToLong(id -> Long.parseLong(id.toString().replace(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM), StringConstants.STRING_BLANK)))
                        .max();
                long max = ol.isPresent() ? ol.getAsLong() : 0;
                user.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM) + String.format("%03d", max + 1)));
            }
        }
        user.setUsername(StringConstants.MANAGER + user.getId());
        user.setStatus(StatusConstants.ENABLE);
        users.add(user);
        MyUtil.writeFile(FileConstants.MANAGER_INFO_PATH, JSON.toJSONString(users));
        System.out.println("添加管理员成功！");
        return true;

    }

    public static void showManagerMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t  *MANAGER MODE*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM_RIGHT + "————————————————————————" + TableConstants.TOP_BOTTOM_LEFT);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.商品查看\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.添加商品\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t3.下架商品\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t4.修改商品信息\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.刷新\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.退出登录\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.print("请选择一个功能：");
        managerFunctionSwitch();
    }

    private static void managerFunctionSwitch() throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
            case "3":
            case "4":
                GoodsView.showMenu();
                break;
            case "2":
                addGoodsInfo();
                break;
            case "9":
                showManagerMenu();
                break;
            case "0":
                Start.showMainMenu();
                break;
            default:
                System.out.println("无此功能，敬请期待！");
                Thread.sleep(3000);
                showManagerMenu();
                break;
        }
    }

    private static void addGoodsInfo() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t   *ADD GOODS*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);

        System.out.println("请输入商品名:");
        String name = Start.sc.nextLine();
        System.out.println("请输入商品价格:");
        String price = Start.sc.nextLine();
        System.out.println("请输入商品单位:");
        String unit = Start.sc.nextLine();
        Product product = new Product(null, name, Double.parseDouble(price), unit);
        File file = new File(FileConstants.GOODS_INFO_PATH);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.out.println("系统异常，商品信息文件创建失败！");
            }
        }

        StringBuilder productList = MyUtil.readFile(FileConstants.GOODS_INFO_PATH);
        List<Product> products = JSONArray.parseArray(productList.toString(), Product.class);
        if (CollectionUtils.isEmpty(products)) {
            products = new ArrayList<>();
        }
        List<Long> ids = products.stream().map(Product::getId).collect(Collectors.toList());
        //生成商品id
        if (CollectionUtils.isEmpty(ids)) {
            product.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%03d", 1)));
        } else {
            List<Long> list = ids.stream()
                    .filter(id -> id.toString().startsWith(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)) {
                product.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%03d", 1)));
            } else {
                OptionalLong ol = list.stream()
                        .mapToLong(id -> Long.parseLong(id.toString().replace(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD), StringConstants.STRING_BLANK)))
                        .max();
                long max = ol.isPresent() ? ol.getAsLong() : 0;
                product.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%03d", max + 1)));
            }
        }
        products.add(product);
        MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
        System.out.println("添加商品成功！");
    }
}
