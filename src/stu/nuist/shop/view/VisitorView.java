package stu.nuist.shop.view;

import stu.nuist.shop.Start;
import stu.nuist.shop.constant.TableConstants;
import stu.nuist.shop.util.MyUtil;

public class VisitorView {

    public static void showMenu() throws Exception {
        MyUtil.clearConsole();
        System.out.println(TableConstants.RIGHT_BOTTOM + "————————————————————————" + TableConstants.LEFT_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\tNUIST SHOP\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t  *VISITOR MODE*\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM_RIGHT + "————————————————————————" + TableConstants.TOP_BOTTOM_LEFT);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t1.Product view\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t2.User registration\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t9.Refresh\t\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.TOP_BOTTOM + "\t\t0.Return to previous layer\t\t " + TableConstants.TOP_BOTTOM);
        System.out.println(TableConstants.RIGHT_TOP + "————————————————————————" + TableConstants.LEFT_TOP);
        System.out.print("Please select a feature：");
        visitorFunctionSwitch();
    }

    private static void visitorFunctionSwitch() throws Exception {
        String key = Start.sc.nextLine();
        switch (key) {
            case "1":
                GoodsView.showMenu();
                break;
            case "2":
                boolean isRegisterSuccess = RegisterView.showMenu();
                while (!isRegisterSuccess) {
                    Thread.sleep(3000);
                    isRegisterSuccess = RegisterView.showMenu();
                }
                Start.showMainMenu();
                break;
            case "9":
                showMenu();
                break;
            case "0":
                Start.showMainMenu();
                break;
            default:
                System.out.println("Without this function, please stay tuned！");
                Thread.sleep(3000);
                showMenu();
                break;
        }
    }
}
