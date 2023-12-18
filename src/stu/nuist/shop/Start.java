package stu.nuist.shop;

import stu.nuist.shop.constant.StringConstants;
import stu.nuist.shop.constant.TableConstants;
import stu.nuist.shop.enity.Detail;
import stu.nuist.shop.enity.User;
import stu.nuist.shop.util.MyUtil;
import stu.nuist.shop.view.ManagerView;
import stu.nuist.shop.view.RegisterView;
import stu.nuist.shop.view.UserView;
import stu.nuist.shop.view.VisitorView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Start {
    public static Scanner sc = new Scanner(System.in);
    public static User loginUser;
    public static User loginManager;
    public static Map<Long, Detail> cart = new HashMap<>();

    public static void main(String[] args) throws Exception {
        showMainMenu();
    }

    public static void showMainMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM_RIGHT + "————————————————————————" + TableConstants.TOP_BOTTOM_LEFT);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.游客登录\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.用户登录\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t3.管理员登录\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t4.用户注册\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.刷新\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.退出\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.print("请选择一个功能：");
        mainFunctionSwitch();
    }

    private static void mainFunctionSwitch() throws Exception {
        String key = sc.nextLine();
        switch (key) {
            case "1":
                VisitorView.showMenu();
                break;
            case "2":
                boolean isLoginSuccess = UserView.showLoginMenu();
                while (!isLoginSuccess) {
                    Thread.sleep(3000);
                    isLoginSuccess = UserView.showLoginMenu();
                }
                if (Objects.isNull(loginUser)) {
                    showMainMenu();
                } else {
                    UserView.showUserMenu();
                }
                break;
            case "3":
                boolean isManagerSuccess = ManagerView.showLoginMenu();
                while (!isManagerSuccess) {
                    Thread.sleep(3000);
                    isManagerSuccess = ManagerView.showLoginMenu();
                }
                if (Objects.isNull(loginManager)) {
                    showMainMenu();
                } else if (StringConstants.SUPER_MANAGER_USERNAME.equals(loginManager.getUsername())) {
                    ManagerView.showSuperManagerMenu();
                } else {
                    ManagerView.showManagerMenu();
                }
                break;
            case "4":
                boolean isRegisterSuccess = RegisterView.showMenu();
                while (!isRegisterSuccess) {
                    Thread.sleep(3000);
                    isRegisterSuccess = RegisterView.showMenu();
                }
                showMainMenu();
                break;
            case "9":
                showMainMenu();
                break;
            case "0":
                System.exit(0);
                break;
            default:
                System.out.println("无此功能，敬请期待！");
                Thread.sleep(3000);
                showMainMenu();
                break;
        }
    }

}
