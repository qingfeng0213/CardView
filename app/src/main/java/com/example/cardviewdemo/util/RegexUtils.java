package com.example.cardviewdemo.util;

import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    private boolean startCheck(String reg, String string) {
        boolean tem = false;
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(string);
        tem = matcher.matches();
        return tem;
    }
    /**
     * 判断手机号是否正确
     *
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        String telRegex = "^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}$";
        return phone.matches(telRegex);
    }

    /**
     * 验证密码格式
     * 6-16位 大小写特殊字符，无划线，横线等
     * @param password
     * @return
     */

    public static boolean checkPassword(String password) {
        String psRegex = "^(?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9]{6,16}$";

        return password.matches(psRegex);
    }
    /**
     * 验证输入框内的银行卡号是否合法
     */
    //验证银行卡号
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }
    //从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 验证用户名
     */
    /**
     * 检验用户名 取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾 用户名有最小长度和最大长度限制，比如用户名必须是4-20位
     * */
    public boolean checkUsername(String username, int min, int max) {
        String regex = "[\\w\u4e00-\u9fa5]{" + min + "," + max + "}(?<!_)";
        return startCheck(regex, username);
    }


    /**
     * 检验空白符
     * */
    public boolean checkWhiteLine(String line) {
        String regex = "(\\s|\\t|\\r)+";

        return startCheck(regex, line);
    }
}
