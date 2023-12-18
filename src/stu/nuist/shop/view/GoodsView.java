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
                System.out.println("系统异常，商品信息文件创建失败！");
            }
        }
        StringBuilder productList = MyUtil.readFile(FileConstants.GOODS_INFO_PATH);
        List<Product> products = JSONArray.parseArray(productList.toString(), Product.class);
        System.out.println("商品信息列表：");
        List<Product> subList = products.subList((pageNum - 1) * pageSize, Math.min(pageNum * pageSize - 1, products.size()));
        for (int i = 0; i < subList.size(); i++) {
            System.out.println((i + 1) + ". " + subList.get(i).toString());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("1.首页\t2.上一页\t3.下一页\t4.尾页\t");
        if (Objects.nonNull(Start.loginUser) || Objects.nonNull(Start.loginManager)) {
            sb.append("8.选择商品进行操作\t");
        }
        sb.append("9.修改页数\t0.返回上一层");
        System.out.println(sb);
        System.out.println("请输入你要进行的选择：");
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
                    System.out.println("请选择你要加入购物车的商品序号:");
                    Product product = subList.get(Integer.parseInt(Start.sc.nextLine()) - 1);
                    showUserGoodsInfo(product);
                    break;
                } else if (Objects.nonNull(Start.loginManager)) {
                    System.out.println("请选择你要修改的商品序号：");
                    Product product = subList.get(Integer.parseInt(Start.sc.nextLine()) - 1);
                    showManagerGoodsInfo(product, products);
                    break;
                }
            case "9":
                System.out.println("请输入你需要修改的单页条数(>0)：");
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
                System.out.println("无此功能，敬请期待！");
                Thread.sleep(3000);
                StringBuilder sb = new StringBuilder();
                sb.append("1.首页\t2.上一页\t3.下一页\t4.尾页\t");
                if (Objects.nonNull(Start.loginUser) || Objects.nonNull(Start.loginManager)) {
                    sb.append("8.选择商品进行操作\t");
                }
                sb.append("9.修改页数\t0.返回上一层");
                System.out.println(sb);
                System.out.println("请输入你要进行的选择：");
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
        System.out.println("1.删除该商品");
        System.out.println("2.修改该商品名称");
        System.out.println("3.修改该商品单价");
        System.out.println("4.修改该商品单位");
        System.out.println("0.返回上一层");
        System.out.println("请选择你要对该商品进行的操作：");
        ManagerGoodsInfoSwitch(product, products);
    }

    private static void ManagerGoodsInfoSwitch(Product product, List<Product> products) throws Exception {
        String key = Start.sc.nextLine();
        int index = 0;
        switch (key) {
            case "1":
                products.remove(product);
                MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
                System.out.println("删除成功！");
                Thread.sleep(2000);
                pageNum = 1;
                showMenu();
                break;
            case "2":
                index = products.indexOf(product);
                System.out.println("请输入你要修改的商品名称：");
                product.setName(Start.sc.nextLine());
                product.setUnit(Start.sc.nextLine());
                products.remove(index);
                products.add(index, product);
                MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
                System.out.println("修改商品名称成功！");
                Thread.sleep(2000);
                pageNum = 1;
                showMenu();
                break;
            case "3":
                index = products.indexOf(product);
                System.out.println("请输入你要修改的商品单价：");
                product.setPrice(Double.parseDouble(Start.sc.nextLine()));
                product.setUnit(Start.sc.nextLine());
                products.remove(index);
                products.add(index, product);
                MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
                System.out.println("修改商品单价成功！");
                Thread.sleep(2000);
                pageNum = 1;
                showMenu();
                break;
            case "4":
                index = products.indexOf(product);
                System.out.println("请输入你要修改的商品单位：");
                product.setUnit(Start.sc.nextLine());
                products.remove(index);
                products.add(index, product);
                MyUtil.writeFile(FileConstants.GOODS_INFO_PATH, JSON.toJSONString(products));
                System.out.println("修改商品单位成功！");
                Thread.sleep(2000);
                pageNum = 1;
                showMenu();
                break;
            case "0":
                showMenu();
                break;
            default:
                System.out.println("无此功能，敬请期待！");
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
        System.out.println("1.直接购入该商品");
        System.out.println("2.将该商品加入购物车");
        System.out.println("0.返回上一层");
        System.out.println("请选择你要对该商品进行的操作：");
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
                System.out.println("请输入你要购入的商品数量：");
                int num = Integer.parseInt(Start.sc.nextLine());
                Detail detail = new Detail(null, product.getId(), num, num * product.getPrice(), null);
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
                System.out.println("商品【" + product.getName() + "】加入购物车成功！生成购物条目【" + detail.getId() + "】成功！");
                Thread.sleep(3000);
                showMenu();
                break;
            case "0":
                showMenu();
                break;
            default:
                System.out.println("无此功能，敬请期待！");
                Thread.sleep(3000);
                showUserGoodsInfo(product);
                break;
        }
    }

    private static void buyAndGenerateOrder(Product product) throws Exception {
        System.out.println("请输入你要购入的商品数量：");
        int num = Integer.parseInt(Start.sc.nextLine());
        Order order = new Order(null, Start.loginUser.getId(), null, num, num * product.getPrice(), DateTime.now().toJdkDate());
        Detail detail = new Detail(null, product.getId(), num, num * product.getPrice(), null);
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
        detail.setOrderId(order.getId());
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
        System.out.println("购买商品【" + product.getName() + "】成功！生成订单【" + order.getId() + "】成功！");
    }
}
