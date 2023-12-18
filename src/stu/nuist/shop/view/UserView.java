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
        System.out.println("请输入用户名:");
        String username = Start.sc.nextLine();
        System.out.println("请输入密码:");
        String pwd = Start.sc.nextLine();
        //验证码
        Random r = new Random();
        int x = r.nextInt(20);
        int y = r.nextInt(20);
        boolean isPlus = r.nextBoolean();
        System.out.println("请输入验证码：【 " + x + (isPlus ? " + " : " - ") + y + " =?】");
        int ans = Integer.parseInt(Start.sc.nextLine());

        File file = new File(FileConstants.USER_INFO_PATH);
        if (file.exists()) {
            StringBuilder userList = MyUtil.readFile(FileConstants.USER_INFO_PATH);
            List<User> users = JSONArray.parseArray(userList.toString(), User.class);
            if (CollectionUtils.isEmpty(users) || !users.stream().map(User::getUsername).collect(Collectors.toList()).contains(username)) {
                System.out.println("此用户名不存在！");
                return false;
            } else if (users.stream().noneMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(pwd))) {
                System.out.println("该用户输入的密码错误！");
                return false;
            }
            User userLog = users.stream().filter(user -> user.getUsername().equals(username) && user.getPassword().equals(pwd)).collect(Collectors.toList()).get(0);
            if (StatusConstants.ENABLE == userLog.getStatus()) {
                Start.loginUser = userLog;
            } else {
                System.out.println("用户状态为" + StringConstants.STATUS_DISABLE + ",无法登录！");
                Thread.sleep(3000);
            }
        } else {
            System.out.println("系统异常，用户信息文件错误！");
            return true;
        }
        int i = isPlus ? x + y : x - y;
        if (i == ans) {
            System.out.println("欢迎用户【" + Start.loginUser.getNickname() + "】登录！");
            Thread.sleep(3000);
        } else {
            System.out.println("验证码错误！");
            return false;
        }
        return true;
    }

    public static void showUserMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t*USER MODE*\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM_RIGHT + "————————————————————————" + TableConstants.TOP_BOTTOM_LEFT);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.商品查看\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.查看购物车\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t3.查看过往订单\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t4.查看用户信息\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.刷新\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.退出登录\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.print("请选择一个功能：");
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
                System.out.println("无此功能，敬请期待！");
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
            System.out.println("该用户现在还未添加商品！");
            return;
        }
        List<Detail> list = details.stream().filter(detail -> Objects.isNull(detail.getOrderId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(Start.cart) && CollectionUtils.isEmpty(list)) {
            System.out.println("该用户现在还未添加商品！");
            return;
        }
        Start.cart = new HashMap<>();
        list.forEach(detail -> Start.cart.put(detail.getProductId(), detail));
        List<Detail> cartDetails = new ArrayList<>(Start.cart.values());
        System.out.println("用户购物车商品信息列表：");
        for (int i = 0; i < cartDetails.size(); i++) {
            System.out.println((i + 1) + ". " + cartDetails.get(i));
        }
        System.out.println("商品总数：" + cartDetails.stream().mapToInt(Detail::getNum).sum() + "\t"
                + "商品总价：" + cartDetails.stream().mapToDouble(Detail::getTotalPrice).sum());
        MyCartSwitch(cartDetails, details);
    }

    private static void MyCartSwitch(List<Detail> cartDetails, List<Detail> details) throws Exception {
        System.out.println();
        System.out.println("1.结算");
        System.out.println("2.删除某个商品");
        System.out.println("3.修改某个商品的数量");
        System.out.println("0.返回");
        System.out.println("请输入你要选择的功能：");
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                generateOrderFromCart(cartDetails);
                Start.cart = new HashMap<>();
                return;
            case "2":
                System.out.println("请输入你要删除的商品编号：");
                String deleteId = Start.sc.nextLine();
                Start.cart.remove(cartDetails.get(Integer.parseInt(deleteId) - 1).getId());
                details.remove(cartDetails.get(Integer.parseInt(deleteId) - 1));
                MyUtil.writeFile(FileConstants.DETAIL_INFO_PATH, JSON.toJSONString(details));
                System.out.println("删除成功！");
                return;
            case "3":
                System.out.println("请输入你要修改的商品编号：");
                String modifyId = Start.sc.nextLine();
                Detail detail = cartDetails.get(Integer.parseInt(modifyId) - 1);
                details.remove(detail);
                System.out.println("请输入你要修改的商品数量：");
                String modifyNum = Start.sc.nextLine();
                detail.setNum(Integer.parseInt(modifyNum));
                Start.cart.replace(detail.getId(), detail);
                details.add(detail);
                MyUtil.writeFile(FileConstants.DETAIL_INFO_PATH, JSON.toJSONString(details));
                System.out.println("修改成功！");
                return;
            case "0":
                return;
            default:
                System.out.println("无此功能，敬请期待！");
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
                System.out.println("系统异常，订单信息文件创建失败！");
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
                System.out.println("系统异常，购物条目信息文件创建失败！");
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
        System.out.println("生成订单【" + order.getId() + "】成功！");
    }

    private static void showMyOrders() throws Exception {
        MyUtil.clearConsole();
        File file = new File(FileConstants.ORDER_INFO_PATH);
        if (!file.exists()) {
            System.out.println("系统异常，订单信息文件读取失败！");
            return;
        }
        StringBuilder orderList = MyUtil.readFile(FileConstants.ORDER_INFO_PATH);
        List<Order> orders = JSONArray.parseArray(orderList.toString(), Order.class);
        if (CollectionUtils.isEmpty(orders)) {
            System.out.println("订单信息列表为空,无法展示！");
            return;
        }
        List<Order> userOrders = orders.stream().filter(order -> order.getUserId().equals(Start.loginUser.getId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userOrders)) {
            System.out.println("该用户的订单信息列表为空,无法展示！");
            return;
        }
        File file2 = new File(FileConstants.DETAIL_INFO_PATH);
        if (!file2.exists()) {
            System.out.println("系统异常，订单详情信息文件读取失败！");
            return;
        }
        StringBuilder detailList = MyUtil.readFile(FileConstants.DETAIL_INFO_PATH);
        List<Detail> details = JSONArray.parseArray(detailList.toString(), Detail.class);
        if (CollectionUtils.isEmpty(details)) {
            System.out.println("订单详情信息列表为空,无法展示！");
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
            System.out.println("是否返回上一层(yes/no)？");
            isStay = Start.sc.nextLine().equals("yes");
        }
    }

    private static void showMyUserInfo() throws Exception {
        MyUtil.clearConsole();
        File file = new File(FileConstants.USER_INFO_PATH);
        if (!file.exists()) {
            System.out.println("系统异常，用户信息文件读取失败！");
            return;
        }
        StringBuilder userList = MyUtil.readFile(FileConstants.USER_INFO_PATH);
        List<User> users = JSONArray.parseArray(userList.toString(), User.class);
        if (CollectionUtils.isEmpty(users)) {
            System.out.println("用户信息列表为空,无法展示！");
            return;
        }
        User u = users.stream().filter(user -> user.getUsername().equals(Start.loginUser.getUsername())).findAny().orElse(null);
        if (Objects.isNull(u)) {
            System.out.println("未查到该用户信息,无法展示！");
        } else {
            MyUtil.clearConsole();
            System.out.println(Start.loginUser);
            System.out.println();
            System.out.println("1.修改昵称");
            System.out.println("2.修改密码");
            System.out.println("3.修改手机号");
            System.out.println("4.修改地址");
            System.out.println("5.销户");
            System.out.println("0.返回上一层");
            System.out.println("请输入你的选择:");
            MyUserInfoSwitch(users);
        }
    }

    private static void MyUserInfoSwitch(List<User> users) throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                System.out.println("请输入你要修改的昵称：");
                String nickName = Start.sc.nextLine();
                if (Objects.isNull(nickName)) {
                    System.out.println("修改的昵称不能为空！");
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
                System.out.println("请输入你要修改的密码：");
                String pwd = Start.sc.nextLine();
                System.out.println("重复你要修改的密码：");
                String rePwd = Start.sc.nextLine();
                if (Objects.isNull(pwd) || Objects.isNull(rePwd)) {
                    System.out.println("修改的密码不能为空！");
                } else if (!pwd.equals(rePwd)) {
                    System.out.println("两次密码输入不一致！");
                } else if (!pwd.matches(RegexConstants.PWD_REGEX) || !rePwd.matches(RegexConstants.PWD_REGEX)) {
                    System.out.println("密码格式错误！");
                } else {
                    users.remove(Start.loginUser);
                    Start.loginUser.setPassword(pwd);
                    users.add(Start.loginUser);
                    MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                    System.out.println("密码修改成功！");
                }
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "3":
                System.out.println("请输入你要修改的手机号：");
                String phone = Start.sc.nextLine();
                if (Objects.isNull(phone)) {
                    System.out.println("修改的手机号不能为空！");
                } else if (!phone.matches("[0-9]{11}")) {
                    System.out.println("手机号码格式错误！");
                } else {
                    users.remove(Start.loginUser);
                    Start.loginUser.setPhone(phone);
                    users.add(Start.loginUser);
                    MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                    System.out.println("手机号修改成功！");
                }
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "4":
                System.out.println("请输入你要修改的地址：");
                String address = Start.sc.nextLine();
                users.remove(Start.loginUser);
                Start.loginUser.setAddress(address);
                users.add(Start.loginUser);
                MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                System.out.println("地址修改成功！");
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "5":
                System.out.println("是否确认要进行销户操作(yes/no)？：");
                boolean isCancel = Start.sc.nextLine().equals("yes");
                if (isCancel) {
                    users.remove(Start.loginUser);
                    Start.loginUser.setStatus(StatusConstants.DISABLE);
                    users.add(Start.loginUser);
                    MyUtil.writeFile(FileConstants.USER_INFO_PATH, JSON.toJSONString(users));
                    System.out.println("用户销户成功！");
                } else {
                    System.out.println("取消销户行为！");
                }
                Thread.sleep(3000);
                showUserMenu();
                break;
            case "0":
                showUserMenu();
                break;
            default:
                System.out.println("无此功能，敬请期待！");
                Thread.sleep(3000);
                showMyUserInfo();
                break;
        }
    }
}
