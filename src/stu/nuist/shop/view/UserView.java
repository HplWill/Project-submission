package stu.nuist.shop.view;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import stu.nuist.shop.Start;
import stu.nuist.shop.constant.*;
import stu.nuist.shop.enity.Detail;
import stu.nuist.shop.enity.Order;
import stu.nuist.shop.enity.User;
import stu.nuist.shop.util.MyUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserView {

    public static boolean showLoginMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t   *LOGIN MODE*\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.println("Please enter your username:");
        String username = Start.sc.nextLine();
        System.out.println("Please enter password:");
        String pwd = Start.sc.nextLine();
        //验证码
        Random r = new Random();
        int x = r.nextInt(20);
        int y = r.nextInt(20);
        boolean isPlus = r.nextBoolean();
        System.out.println("Please enter the verification code：【 " + x + (isPlus ? " + " : " - ") + y + " =?】");
        int ans = Integer.parseInt(Start.sc.nextLine());

        File file = new File(FileConstants.USER_INFO_PATH);
        if (file.exists()) {
            StringBuilder userList = MyUtil.readFile(FileConstants.USER_INFO_PATH);
            List<User> users = JSONArray.parseArray(userList.toString(), User.class);
            if (CollectionUtils.isEmpty(users) || !users.stream().map(User::getUsername).collect(Collectors.toList()).contains(username)) {
                System.out.println("This user name does not exist！");
                return false;
            } else if (users.stream().noneMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(pwd))) {
                System.out.println("The user entered an incorrect password！");
                return false;
            }
            User userLog = users.stream().filter(user -> user.getUsername().equals(username) && user.getPassword().equals(pwd)).collect(Collectors.toList()).get(0);
            if (StatusConstants.ENABLE == userLog.getStatus()) {
                Start.loginUser = userLog;
            } else {
                System.out.println("User status is" + StringConstants.STATUS_DISABLE + ",Unable to log in！");
                Thread.sleep(3000);
            }
        } else {
            System.out.println("The system is abnormal, and the user information file is incorrect！");
            return true;
        }
        int i = isPlus ? x + y : x - y;
        if (i == ans) {
            System.out.println("Welcome users【" + Start.loginUser.getNickname() + "】Log in!");
            Thread.sleep(3000);
        } else {
            System.out.println("Verification code error！");
            return false;
        }
        return true;
    }

    public static void showUserMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t*USER MODE*\t\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM_RIGHT + "————————————————————————————————" + TableConstants.TOP_BOTTOM_LEFT);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.Product view\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.View cart\t\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t3.View past orders\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t4.View  User Information " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.Refresh\t\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.Log out\t\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————————————" + TableConstants.LEFT_TOP);
            System.out.print("Please select a feature：");
        userFunctionSwitch();
    }

    private static void userFunctionSwitch() throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                GoodsView.showMenu();
                break;
            case "2":
                showMyCart();
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "3":
                showMyOrders();
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "4":
                showMyUserInfo();
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "9":
                showUserMenu();
                break;
            case "0":
                Start.showMainMenu();
                break;
            default:
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                showUserMenu();
                break;
        }
    }

    private static void showMyCart() throws Exception {
        MyUtil.clearConsole();
        StringBuilder detailList = MyUtil.readFile(FileConstants.DETAIL_INFO_PATH);
        List<Detail> details = JSONArray.parseArray(detailList.toString(), Detail.class);
        if (CollectionUtils.isEmpty(details)) {
            System.out.println("The user has not added an item yet！");
            return;
        }
        List<Detail> list = details.stream().filter(detail -> Objects.isNull(detail.getOrderId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(Start.cart) && CollectionUtils.isEmpty(list)) {
            System.out.println("The user has not added an item yet！");
            return;
        }
        Start.cart = new HashMap<>();
        list.forEach(detail -> Start.cart.put(detail.getProductId(), detail));
        List<Detail> cartDetails = new ArrayList<>(Start.cart.values());
        System.out.println("User shopping cart item information list：");
        for (int i = 0; i < cartDetails.size(); i++) {
            System.out.println((i + 1) + ". " + cartDetails.get(i));
        }
        System.out.println("Total number of commodities：" + cartDetails.stream().mapToInt(Detail::getNum).sum() + "\t"
                + "Gross commodity price：" + cartDetails.stream().mapToDouble(Detail::getTotalPrice).sum());
        MyCartSwitch(cartDetails, details);
    }

    private static void MyCartSwitch(List<Detail> cartDetails, List<Detail> details) throws Exception {
        System.out.println();
        System.out.println("1.Settle an account");
        System.out.println("2.Delete an item");
        System.out.println("3.Modify the quantity of an item");
        System.out.println("0.Back");
        System.out.println("Please enter the function you want to select：");
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                generateOrderFromCart(cartDetails);
                Start.cart = new HashMap<>();
                return;
            case "2":
                    System.out.println("Please enter the item number you want to delete：");
                String deleteId = Start.sc.nextLine();
                Start.cart.remove(cartDetails.get(Integer.parseInt(deleteId) - 1).getId());
                details.remove(cartDetails.get(Integer.parseInt(deleteId) - 1));
                MyUtil.writeFile(FileConstants.DETAIL_INFO_PATH, JSON.toJSONString(details));
                System.out.println("Successfully deleted！");
                return;
            case "3":
                System.out.println("Please enter the item number you want to modify：");
                String modifyId = Start.sc.nextLine();
                Detail detail = cartDetails.get(Integer.parseInt(modifyId) - 1);
                details.remove(detail);
                System.out.println("Please enter the quantity of goods you want to modify：");
                String modifyNum = Start.sc.nextLine();
                detail.setNum(Integer.parseInt(modifyNum));
                Start.cart.replace(detail.getId(), detail);
                details.add(detail);
                MyUtil.writeFile(FileConstants.DETAIL_INFO_PATH, JSON.toJSONString(details));
                System.out.println("Modified successfully！");
                return;
            case "0":
                return;
            default:
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                MyCartSwitch(cartDetails, details);
        }
    }

    private static void generateOrderFromCart(List<Detail> cartDetails) throws Exception {
        Order order = new Order(null, Start.loginUser.getId(), null, cartDetails.stream().mapToInt(Detail::getNum).sum(),
                cartDetails.stream().mapToDouble(Detail::getTotalPrice).sum(), DateTime.now().toJdkDate());
        File file = new File(FileConstants.ORDER_INFO_PATH);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.out.println("The system is abnormal. The order information file fails to be created！");
            }
        }
        StringBuilder orderList = MyUtil.readFile(FileConstants.ORDER_INFO_PATH);
        List<Order> orders = JSONArray.parseArray(orderList.toString(), Order.class);
        if (CollectionUtils.isEmpty(orders)) {
            orders = new ArrayList<>();
        }
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        //生成订单id
        if (CollectionUtils.isEmpty(orderIds)) {
            order.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", 1)));
        } else {
            List<Long> list = orderIds.stream()
                    .filter(id -> id.toString().startsWith(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)) {
                order.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", 1)));
            } else {
                OptionalLong ol = list.stream()
                        .mapToLong(id -> Long.parseLong(id.toString().replace(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD), StringConstants.STRING_BLANK)))
                        .max();
                long max = ol.isPresent() ? ol.getAsLong() : 0;
                order.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", max + 1)));
            }
        }
        order.setNo("no" + order.getId());
        File file2 = new File(FileConstants.DETAIL_INFO_PATH);
        if (!file2.exists()) {
            boolean newFile = file2.createNewFile();
            if (!newFile) {
                System.out.println("Description The system is abnormal. Failed to create the shopping item information file！");
            }
        }
        StringBuilder detailList = MyUtil.readFile(FileConstants.DETAIL_INFO_PATH);
        List<Detail> details = JSONArray.parseArray(detailList.toString(), Detail.class);
        if (CollectionUtils.isEmpty(details)) {
            details = new ArrayList<>();
        }
        details.removeAll(details.stream().filter(detail -> Objects.isNull(detail.getOrderId())).collect(Collectors.toList()));
        List<Long> detailIds = details.stream().map(Detail::getId).collect(Collectors.toList());
        //生成购物条目id
        if (CollectionUtils.isEmpty(detailIds)) {
            for (int i = 0; i < cartDetails.size(); i++) {
                cartDetails.get(i).setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", i + 1)));
            }
        } else {
            List<Long> list = detailIds.stream()
                    .filter(id -> id.toString().startsWith(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)) {
                for (int i = 0; i < cartDetails.size(); i++) {
                    cartDetails.get(i).setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", i + 1)));
                }
            } else {
                OptionalLong ol = list.stream()
                        .mapToLong(id -> Long.parseLong(id.toString().replace(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD), StringConstants.STRING_BLANK)))
                        .max();
                long max = ol.isPresent() ? ol.getAsLong() : 0;
                for (int i = 0; i < cartDetails.size(); i++) {
                    cartDetails.get(i).setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", max + i + 1)));
                }
            }
        }
        cartDetails.forEach(d -> d.setOrderId(order.getId()));
        orders.add(order);
        details.addAll(cartDetails);
        MyUtil.writeFile(FileConstants.ORDER_INFO_PATH, JSON.toJSONString(orders));
        MyUtil.writeFile(FileConstants.DETAIL_INFO_PATH, JSON.toJSONString(details));
        System.out.println("Generate order【" + order.getId() + "】Successful！");
    }

    private static void showMyOrders() throws Exception {
        MyUtil.clearConsole();
        File file = new File(FileConstants.ORDER_INFO_PATH);
        if (!file.exists()) {
            System.out.println("The system is abnormal. The order information file fails to be read！");
            return;
        }
        StringBuilder orderList = MyUtil.readFile(FileConstants.ORDER_INFO_PATH);
        List<Order> orders = JSONArray.parseArray(orderList.toString(), Order.class);
        if (CollectionUtils.isEmpty(orders)) {
            System.out.println("The order information list is empty and cannot be displayed！");
            return;
        }
        List<Order> userOrders = orders.stream().filter(order -> order.getUserId().equals(Start.loginUser.getId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userOrders)) {
            System.out.println("The order information list for this user is empty and cannot be displayed！");
            return;
        }
        File file2 = new File(FileConstants.DETAIL_INFO_PATH);
        if (!file2.exists()) {
            System.out.println("The system is abnormal. The order details file failed to be read！");
            return;
        }
        StringBuilder detailList = MyUtil.readFile(FileConstants.DETAIL_INFO_PATH);
        List<Detail> details = JSONArray.parseArray(detailList.toString(), Detail.class);
        if (CollectionUtils.isEmpty(details)) {
            System.out.println("The order details list is empty and cannot be displayed！");
            return;
        }
        for (int i = 0; i < userOrders.size(); i++) {
            int finalI = i;
            List<Detail> orderDetails = details.stream().filter(detail -> detail.getOrderId().equals(userOrders.get(finalI).getId())).collect(Collectors.toList());
            System.out.println((i + 1) + ". " + userOrders.get(finalI).toString());
            for (int j = 0; j < orderDetails.size(); j++) {
                System.out.println("——" + (j + 1) + ". " + orderDetails.get(j).toString());
            }
        }
        boolean isStay = false;
        while (!isStay) {
            System.out.println("Whether to return to the previous layer(yes/no)？");
            isStay = Start.sc.nextLine().equals("yes");
        }
    }

    private static void showMyUserInfo() throws Exception {
        MyUtil.clearConsole();
        File file = new File(FileConstants.USER_INFO_PATH);
        if (!file.exists()) {
            System.out.println("The system is abnormal. The user information file fails to be read！");
            return;
        }
        StringBuilder userList = MyUtil.readFile(FileConstants.USER_INFO_PATH);
        List<User> users = JSONArray.parseArray(userList.toString(), User.class);
        if (CollectionUtils.isEmpty(users)) {
            System.out.println("The user information list is empty and cannot be displayed！");
            return;
        }
        User u = users.stream().filter(user -> user.getUsername().equals(Start.loginUser.getUsername())).findAny().orElse(null);
        if (Objects.isNull(u)) {
            System.out.println("The user information is not found and cannot be displayed！");
        } else {
            MyUtil.clearConsole();
            System.out.println(Start.loginUser);
            System.out.println();
            System.out.println("1.Change a nickname");
            System.out.println("2.Change password");
            System.out.println("3.Modify mobile phone number");
            System.out.println("4.Modify address");
            System.out.println("5.Account cancellation");
            System.out.println("0.Return to previous layer");
            System.out.println("Please enter your choice:");
            MyUserInfoSwitch(users);
        }
    }

    private static void MyUserInfoSwitch(List<User> users) throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                System.out.println("Please enter the nickname you want to change：");
                String nickName = Start.sc.nextLine();
                if (Objects.isNull(nickName)) {
                    System.out.println("The modified nickname cannot be empty！");
                } else {
                    users.remove(Start.loginUser);
                    Start.loginUser.setNickname(nickName);
                    users.add(Start.loginUser);
                    MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                    System.out.println("昵称修改成功！");
                }
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "2":
                System.out.println("Please enter the password you want to change：");
                String pwd = Start.sc.nextLine();
                System.out.println("Repeat the password you want to change：");
                String rePwd = Start.sc.nextLine();
                if (Objects.isNull(pwd) || Objects.isNull(rePwd)) {
                    System.out.println("The new password cannot be empty！");
                } else if (!pwd.equals(rePwd)) {
                    System.out.println("The two password entries are inconsistent！");
                } else if (!pwd.matches(RegexConstants.PWD_REGEX) || !rePwd.matches(RegexConstants.PWD_REGEX)) {
                    System.out.println("Password format error！");
                } else {
                    users.remove(Start.loginUser);
                    Start.loginUser.setPassword(pwd);
                    users.add(Start.loginUser);
                    MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                    System.out.println("Password changed successfully！");
                }
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "3":
                System.out.println("Please enter the phone number you want to change：");
                String phone = Start.sc.nextLine();
                if (Objects.isNull(phone)) {
                    System.out.println("The changed mobile phone number cannot be empty！");
                } else if (!phone.matches("[0-9]{11}")) {
                    System.out.println("The mobile phone number format is incorrect！");
                } else {
                    users.remove(Start.loginUser);
                    Start.loginUser.setPhone(phone);
                    users.add(Start.loginUser);
                    MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                    System.out.println("The mobile phone number is successfully changed！");
                }
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "4":
                System.out.println("Please enter the address you want to change：");
                String address = Start.sc.nextLine();
                users.remove(Start.loginUser);
                Start.loginUser.setAddress(address);
                users.add(Start.loginUser);
                MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                System.out.println("Address modification succeeded！");
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "5":
                System.out.println("Confirm whether to perform account cancellation operation(yes/no)？：");
                boolean isCancel = Start.sc.nextLine().equals("yes");
                if (isCancel) {
                    users.remove(Start.loginUser);
                    Start.loginUser.setStatus(StatusConstants.DISABLE);
                    users.add(Start.loginUser);
                    MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                    System.out.println("The user successfully logged out the account！");
                } else {
                    System.out.println("Cancel account cancellation！");
                }
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "0":
                showUserMenu();
                break;
            default:
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                showMyUserInfo();
                break;
        }
    }
}
