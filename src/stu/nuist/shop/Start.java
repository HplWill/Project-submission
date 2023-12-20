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
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM_RIGHT + "————————————————————————————" + TableConstants.TOP_BOTTOM_LEFT);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.Visitor login\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.User login\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t3.Administrator login" + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t4.User registration\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.Refresh\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.quit\t\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————————" + TableConstants.LEFT_TOP);
        System.out.print("Please select a feature：");
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
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                showMainMenu();
                break;
        }
    }

}
