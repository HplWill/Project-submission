package stu.nuist.shop.view;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import stu.nuist.shop.Start;
import stu.nuist.shop.constant.FileConstants;
import stu.nuist.shop.constant.StringConstants;
import stu.nuist.shop.constant.TableConstants;
import stu.nuist.shop.enity.Detail;
import stu.nuist.shop.enity.Order;
import stu.nuist.shop.enity.Product;
import stu.nuist.shop.util.MyUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Collectors;

public class GoodsView {
    public static Integer pageNum = 1;
    public static Integer pageSize = 10;

    public static void showMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t   *GOODS MODE*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        File file = new File(FileConstants.GOODS_INFO_PATH);
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.out.println("The product information file fails to be created because the system is abnormal！");
            }
        }
        StringBuilder productList = MyUtil.readFile(FileConstants.GOODS_INFO_PATH);
        List<Product> products = JSONArray.parseArray(productList.toString(), Product.class);
        System.out.println("Product information list：");
        List<Product> subList = products.subList((pageNum - 1) * pageSize, Math.min(pageNum * pageSize - 1, products.size()));
        for (int i = 0; i < subList.size(); i++) {
            System.out.println((i + 1) + ". " + subList.get(i).toString());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("1.Home page\t2.Previous\t3.Next\t4.Trailing page\t");
        if (Objects.nonNull(Start.loginUser) || Objects.nonNull(Start.loginManager)) {
            sb.append("8.Select the item to operate\t");
        }
        sb.append("9.Page change\t0.Return to previous layer");
        System.out.println(sb);
        System.out.println("Please enter the selection you want to make：");
        productInfoSwitch(products, subList);
    }

    private static void productInfoSwitch(List<Product> products, List<Product> subList) throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                pageNum = 1;
                showMenu();
                break;
            case "2":
                pageNum = Math.max(pageNum - 1, 1);
                showMenu();
                break;
            case "3":
                pageNum = Math.min(products.size() == 0 ? 1 : NumberUtil.ceilDiv(products.size(), pageSize), pageNum + 1);
                showMenu();
                break;
            case "4":
                pageNum = products.size() == 0 ? 1 : NumberUtil.ceilDiv(products.size(), pageSize);
                showMenu();
                break;
            case "8":
                if (Objects.nonNull(Start.loginUser)) {
                    System.out.println("Please select the item number you want to add to your cart:");
                    Product product = subList.get(Integer.parseInt(Start.sc.nextLine()) - 1);
                    showUserGoodsInfo(product);
                    break;
                } else if (Objects.nonNull(Start.loginManager)) {
                    System.out.println("Please select the item number you want to modify：");
                    Product product = subList.get(Integer.parseInt(Start.sc.nextLine()) - 1);
                    showManagerGoodsInfo(product, products);
                    break;
                }
            case "9":
                System.out.println("Please enter the number of pages you want to modify(>0)：");
                pageSize = Integer.parseInt(Start.sc.nextLine());
                showMenu();
                break;
            case "0":
                if (Start.loginUser != null) {
                    UserView.showUserMenu();
                } else if (Start.loginManager != null) {
                    ManagerView.showManagerMenu();
                } else {
                    Start.showMainMenu();
                }
                break;
            default:
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                StringBuilder sb = new StringBuilder();
                sb.append("1.Home page\t2.Previous\t3.Next\t4.Trailing page\t");
                if (Objects.nonNull(Start.loginUser) || Objects.nonNull(Start.loginManager)) {
                    sb.append("8.Select the item to operate\t");
                }
                sb.append("9.Page change\t0.Return to previous layer");
                System.out.println(sb);
                System.out.println("Please enter the selection you want to make：");
                productInfoSwitch(products, subList);
                break;
        }
    }

    private static void showManagerGoodsInfo(Product product, List<Product> products) throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t *OPERATE GOODS*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.println(product.toString());
        System.out.println();
        System.out.println("1.Delete the product");
        System.out.println("2.Modify the product name");
        System.out.println("3.Modify the unit price of the item");
        System.out.println("4.Modify the commodity unit");
        System.out.println("0.Return to previous layer");
        System.out.println("Please select what you want to do with the product：");
        ManagerGoodsInfoSwitch(product, products);
    }

    private static void ManagerGoodsInfoSwitch(Product product, List<Product> products) throws Exception {
        String key = Start.sc.nextLine();
        int index = 0;
        switch (key) {
            case "1":
                products.remove(product);
                MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
                System.out.println("Successfully deleted！");
                Thread.sleep(2000);
                pageNum = 1;
                showMenu();
                break;
            case "2":
                index = products.indexOf(product);
                System.out.println("Please enter the name of the product you want to modify：");
                product.setName(Start.sc.nextLine());
                product.setUnit(Start.sc.nextLine());
                products.remove(index);
                products.add(index, product);
                MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
                System.out.println("Modifying the product name succeeded！");
                Thread.sleep(2000);
                pageNum = 1;
                showMenu();
                break;
            case "3":
                index = products.indexOf(product);
                System.out.println("Please enter the unit price of the item you want to modify：");
                product.setPrice(Double.parseDouble(Start.sc.nextLine()));
                product.setUnit(Start.sc.nextLine());
                products.remove(index);
                products.add(index, product);
                MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
                System.out.println("Modifying the unit price succeeded！");
                Thread.sleep(2000);
                pageNum = 1;
                showMenu();
                break;
            case "4":
                index = products.indexOf(product);
                System.out.println("Please enter the product unit you want to modify：");
                product.setUnit(Start.sc.nextLine());
                products.remove(index);
                products.add(index, product);
                MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
                System.out.println("Modifying the commodity unit succeeded！");
                Thread.sleep(2000);
                pageNum = 1;
                showMenu();
                break;
            case "0":
                showMenu();
                break;
            default:
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                showManagerGoodsInfo(product, products);
                break;
        }
    }

    private static void showUserGoodsInfo(Product product) throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t  *BUY GOODS*\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.println(product.toString());
        System.out.println();
        System.out.println("1.Direct purchase of the goods");
        System.out.println("2.Add the item to the cart");
        System.out.println("0.Return to previous layer");
        System.out.println("Please select what you want to do with the product：");
        UserGoodsInfoSwitch(product);
    }

    private static void UserGoodsInfoSwitch(Product product) throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                buyAndGenerateOrder(product);
                Thread.sleep(3000);
                showMenu();
                break;
            case "2":
                System.out.println("Please enter the quantity of the goods you want to purchase：");
                int num = Integer.parseInt(Start.sc.nextLine());
                Detail detail = new Detail(null, product.getId(), num, num * product.getPrice(), null);
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
                List<Long> detailIds = details.stream().map(Detail::getId).collect(Collectors.toList());
                //生成购物条目id
                if (CollectionUtils.isEmpty(detailIds)) {
                    detail.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", 1)));
                } else {
                    Detail existDetail = details.stream().filter(d -> d.getProductId().equals(product.getId()) && Objects.isNull(d.getOrderId()))
                            .findAny().orElse(null);
                    if (Objects.nonNull(existDetail)) {
                        Start.cart.merge(product.getId(), detail, (o, n) -> {
                            n.setNum(n.getNum() + o.getNum());
                            n.setTotalPrice(n.getNum() * product.getPrice());
                            return n;
                        });
                        details.remove(existDetail);
                        detail = Start.cart.get(product.getId());
                    } else {
                        List<Long> list = detailIds.stream()
                                .filter(id -> id.toString().startsWith(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD)))
                                .collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(list)) {
                            detail.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", 1)));
                        } else {
                            OptionalLong ol = list.stream()
                                    .mapToLong(id -> Long.parseLong(id.toString().replace(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD), StringConstants.STRING_BLANK)))
                                    .max();
                            long max = ol.isPresent() ? ol.getAsLong() : 0;
                            detail.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", max + 1)));
                        }
                        Start.cart.put(product.getId(), detail);
                    }
                }
                details.add(detail);
                MyUtil.writeFile(FileConstants.DETAIL_INFO_PATH, JSON.toJSONString(details));
                System.out.println("product【" + product.getName() + "】Add to cart successfully！Generate shopping items【" + detail.getId() + "】Successful！");
                Thread.sleep(3000);
                showMenu();
                break;
            case "0":
                showMenu();
                break;
            default:
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                showUserGoodsInfo(product);
                break;
        }
    }

    private static void buyAndGenerateOrder(Product product) throws Exception {
        System.out.println("Please enter the quantity of the goods you want to purchase：");
        int num = Integer.parseInt(Start.sc.nextLine());
        Order order = new Order(null, Start.loginUser.getId(), null, num, num * product.getPrice(), DateTime.now().toJdkDate());
        Detail detail = new Detail(null, product.getId(), num, num * product.getPrice(), null);
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
        detail.setOrderId(order.getId());
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
        List<Long> detailIds = details.stream().map(Detail::getId).collect(Collectors.toList());
        //生成购物条目id
        if (CollectionUtils.isEmpty(detailIds)) {
            detail.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", 1)));
        } else {
            List<Long> list = detailIds.stream()
                    .filter(id -> id.toString().startsWith(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(list)) {
                detail.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", 1)));
            } else {
                OptionalLong ol = list.stream()
                        .mapToLong(id -> Long.parseLong(id.toString().replace(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD), StringConstants.STRING_BLANK)))
                        .max();
                long max = ol.isPresent() ? ol.getAsLong() : 0;
                detail.setId(Long.parseLong(DateUtil.format(LocalDateTime.now(), StringConstants.SDF_YYYY_MM_DD) + String.format("%04d", max + 1)));
            }
        }
        orders.add(order);
        details.add(detail);
        MyUtil.writeFile(FileConstants.ORDER_INFO_PATH, JSON.toJSONString(orders));
        MyUtil.writeFile(FileConstants.DETAIL_INFO_PATH, JSON.toJSONString(details));
        System.out.println("Purchase goods【" + product.getName() + "】Successful！Generate order【" + order.getId() + "】Successful！");
    }
}
