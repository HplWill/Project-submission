package stu.nuist.shop.util;

import stu.nuist.shop.constant.StringConstants;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyUtil {

    public static void clearScreen() {
        String os = System.getProperty(StringConstants.OS_NAME).toLowerCase();
        if (os.contains(StringConstants.WIN)) {
            try {
                new ProcessBuilder(StringConstants.CMD, "/c", StringConstants.CLS).inheritIO().start().waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else if (os.contains(StringConstants.NIX) | os.contains(StringConstants.NUX) | os.contains(StringConstants.MAC)) {
            System.out.print(StringConstants.CLEAN_CODE);
            System.out.flush();
        }
    }

    public static void clearConsole() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_R);
        robot.keyRelease(KeyEvent.VK_R);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(20);
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static StringBuilder readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName), Charset.forName(StringConstants.CHARSET_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void writeFile(String fileName, String jsonStr) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(fileName), Charset.forName(StringConstants.CHARSET_NAME))) {
            bw.write(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
