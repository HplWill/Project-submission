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
        System.out.println("Please enter your username:");
        String username = Start.sc.nextLine();
        System.out.println("Please enter password:");
        String pwd = Start.sc.nextLine();

        File file = new File(FileConstants.MANAGER_INFO_PATH);
        if (file.exists()) {
            StringBuilder userList = MyUtil.readFile(FileConstants.MANAGER_INFO_PATH);
            List<User> users = JSONArray.parseArray(userList.toString(), User.class);
            if (CollectionUtils.isEmpty(users) || !users.stream()
                    .map(User::getUsername).collect(Collectors.toList()).contains(username)) {
                System.out.println("The administrator user name does not exist！");
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
                System.out.println("The status of the administrator user is" + StringConstants.STATUS_DISABLE + ",Unable to log in！");
                Thread.sleep(3000);
            }
        } else {
            System.out.println("The system is abnormal, and the user information file is incorrect！");
            return true;
        }
        System.out.println("Welcome administrator【" + Start.loginManager.getNickname() + "】Log in！");
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
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.User view\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.Administrator view\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t3.Add an administrator\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.Refresh\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.Log out\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.print("Please select a feature：");
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
                System.out.println("Without this function, please stay tuned！");
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
                System.out.println("The user information list is empty and cannot be displayed！");
                Thread.sleep(3000);
                showSuperManagerMenu();
            } else {
                System.out.println("User information list：");
                for (int i = 0; i < users.size(); i++) {
                    System.out.println((i + 1) + ". " + users.get(i).toString());
                }
                System.out.println();
                System.out.println("Whether to perform operations on the specified user？(no-Return to the supertube menu, enter the list number for operation selection)：");
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
                System.out.println("The user information list is empty and cannot be displayed！");
            } else {
                System.out.println("The user information file fails to be created because the system is abnormal！");
            }
            Thread.sleep(3000);
            showSuperManagerMenu();
        }
    }

    private static void operateUserInfo(List<User> users, int i) throws Exception {
        MyUtil.clearConsole();
        System.out.println(users.get(i).toString());
        System.out.println();
        System.out.println("1.Modifying User status");
        System.out.println("0.Back");
        System.out.println("Please select what you want to do with this user information：");
        operateUserInfoSwitch(users, i);
    }

    private static void operateUserInfoSwitch(List<User> users, int i) throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                users.get(i).setStatus(-1 * users.get(i).getStatus());
                MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                System.out.println("user【" + users.get(i).getId() + "】Status modified successfully！");
                Thread.sleep(3000);
                showSuperManagerMenu();
                break;
            case "0":
                showSuperManagerMenu();
                break;
            default:
                System.out.println("Without this function, please stay tuned！");
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
                System.out.println("The administrator information list is empty and cannot be displayed！");
                Thread.sleep(3000);
                showSuperManagerMenu();
            } else {
                System.out.println("Administrator information list：");
                for (int i = 0; i < users.size(); i++) {
                    System.out.println((i + 1) + ". " + users.get(i).toString());
                }
                System.out.println();
                System.out.println("Whether to perform operations on the specified administrator？(no-Return to the supertube menu, enter the list number for operation selection)：");
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
                System.out.println("The administrator information list is empty and cannot be displayed！");
            } else {
                System.out.println("The system is abnormal. The administrator information file fails to be created. Procedure！");
            }
            Thread.sleep(3000);
            showSuperManagerMenu();
        }
    }

    private static void operateManagerInfo(List<User> users, int i) throws Exception {
        if (StringConstants.SUPER_MANAGER_USERNAME.equals(users.get(i).getUsername())) {
            System.out.println("You can't manipulate your supertube！");
            Thread.sleep(1500);
            System.out.println("Whether to perform operations on the specified administrator？(no-Return to the supertube menu, enter the list number for operation selection)：");
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
        System.out.println("1.Modifying Administrator Status");
        System.out.println("2.Changing the Administrator Password");
        System.out.println("3.Changing an administrator nickname");
        System.out.println("0.back");
        System.out.println("Please select what you want to do with this user information：");
        operateManagerInfoSwitch(users, i);
    }

    private static void operateManagerInfoSwitch(List<User> users, int i) throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                users.get(i).setStatus(-1 * users.get(i).getStatus());
                MyUtil.writeFile(FileConstants.MANAGER_INFO_PATH, JSON.toJSONString(users));
                System.out.println("Administrator【" + users.get(i).getId() + "】Status modified successfully！");
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
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                operateManagerInfo(users, i);
                break;
        }
    }

    private static void modifyMangerPassword(List<User> users, int i) throws Exception {
        System.out.println("Please enter your new password：");
        String pwd = Start.sc.nextLine();
        if (Objects.isNull(pwd)) {
            System.out.println("The password cannot be empty！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else if (!pwd.matches(RegexConstants.PWD_REGEX)) {
            System.out.println("Password format error！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else if (pwd.equals(users.get(i).getPassword())) {
            System.out.println("The password cannot be the same as the old password！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else {
            users.get(i).setPassword(pwd);
            MyUtil.writeFile(FileConstants.MANAGER_INFO_PATH, JSON.toJSONString(users));
            System.out.println("Administrator【" + users.get(i).getId() + "】Password changed successfully！");
            Thread.sleep(3000);
            showSuperManagerMenu();
        }
    }

    private static void modifyMangerNickName(List<User> users, int i) throws Exception {
        System.out.println("Please enter the nickname you want to change：");
        String nickName = Start.sc.nextLine();
        if (Objects.isNull(nickName)) {
            System.out.println("Nicknames cannot be empty！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else if (nickName.equals(users.get(i).getNickname())) {
            System.out.println("The nickname cannot be the same as the original nickname！");
            Thread.sleep(2000);
            showOperateManagerInfo(users, i);
        } else {
            users.get(i).setNickname(nickName);
            MyUtil.writeFile(FileConstants.MANAGER_INFO_PATH, JSON.toJSONString(users));
            System.out.println("Administrator【" + users.get(i).getId() + "】Nickname modified successfully！");
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
        User user = new User(null, null, pwd, nickname, phone, address, null);
        File file = new File(FileConstants.MANAGER_INFO_PATH);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.out.println("The user information file fails to be created because the system is abnormal！");
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
        System.out.println("Succeeded in adding an administrator！");
        return true;

    }

    public static void showManagerMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t  *MANAGER MODE*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM_RIGHT + "————————————————————————" + TableConstants.TOP_BOTTOM_LEFT);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.Product view\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.Add goods\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t3.Removed goods\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t4.Modify product information\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.Refresh\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.Log out\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.print("Please select a feature：");
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
                System.out.println("Without this function, please stay tuned！");
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

        System.out.println("Please enter the product name:");
        String name = Start.sc.nextLine();
        System.out.println("Please enter the price of the item:");
        String price = Start.sc.nextLine();
        System.out.println("Please enter the product unit:");
        String unit = Start.sc.nextLine();
        Product product = new Product(null, name, Double.parseDouble(price), unit);
        File file = new File(FileConstants.GOODS_INFO_PATH);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.out.println("The product information file fails to be created because the system is abnormal！");
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
        System.out.println("Item added successfully！");
    }
}
