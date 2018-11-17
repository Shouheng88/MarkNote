package me.shouheng.commons.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shouh
 * @version $Id: StringUtils, v 0.1 2018/6/6 22:14 shouh Exp$
 */
public class StringUtils {

    private static final String SEP1 = "#";

    private static final String SEP2 = "|";

    private static final String SEP3 = "=";

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_PHONE_REGEX =
            Pattern.compile("(^(00){0,1}(13\\\\d|15[^4,\\\\D]|17[13678]|18\\\\d)\\\\d{8}|170[^346,\\\\D]\\\\d{7}$)", Pattern.CASE_INSENSITIVE);

    public static String ListToString(List<?> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || "".equals(list.get(i))) {
                    continue;
                }
                // 如果值是list类型则调用自己
                if (list.get(i) instanceof List) {
                    sb.append(ListToString((List<?>) list.get(i)));
                    sb.append(SEP1);
                } else if (list.get(i) instanceof Map) {
                    sb.append(MapToString((Map<?, ?>) list.get(i)));
                    sb.append(SEP1);
                } else {
                    sb.append(list.get(i));
                    sb.append(SEP1);
                }
            }
        }
        return "L" + sb.toString();
    }

    public static String MapToString(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        // 遍历map
        for (Object obj : map.keySet()) {
            if (obj == null) {
                continue;
            }
            Object value = map.get(obj);
            if (value instanceof List<?>) {
                sb.append(obj.toString()).append(SEP1).append(ListToString((List<?>) value));
                sb.append(SEP2);
            } else if (value instanceof Map<?, ?>) {
                sb.append(obj.toString()).append(SEP1).append(MapToString((Map<?, ?>) value));
                sb.append(SEP2);
            } else {
                sb.append(obj.toString()).append(SEP3).append(value.toString());
                sb.append(SEP2);
            }
        }
        return "M" + sb.toString();
    }

    public static Map<String, Object> StringToMap(String mapText) {
        if (mapText == null || mapText.equals("")) return null;

        mapText = mapText.substring(1);

        Map<String, Object> map = new HashMap<>();
        String[] text = mapText.split("\\" + SEP2); // 转换为数组
        for (String str : text) {
            String[] keyText = str.split(SEP3); // 转换key与value的数组
            if (keyText.length < 1) {
                continue;
            }
            String key = keyText[0]; // key
            String value = keyText[1]; // value
            if (value.charAt(0) == 'M') {
                Map<?, ?> map1 = StringToMap(value);
                map.put(key, map1);
            } else if (value.charAt(0) == 'L') {
                List<?> list = StringToList(value);
                map.put(key, list);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    public static List<Object> StringToList(String listText) {
        if (listText == null || listText.equals("")) {
            return null;
        }
        listText = listText.substring(1);

        List<Object> list = new LinkedList<>();
        String[] text = listText.split(SEP1);
        for (String str : text) {
            if (str.charAt(0) == 'M') {
                Map<?, ?> map = StringToMap(str);
                list.add(map);
            } else if (str.charAt(0) == 'L') {
                List<?> lists = StringToList(str);
                list.add(lists);
            } else {
                list.add(str);
            }
        }
        return list;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static Object convertFromString(String text) {
        byte[] bytes = hexStringToByteArray(text);
        ObjectInputStream in = null;

        try {
            in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return in.readObject();
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtils.e(e);
                }
            }
        }
        return null;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNotChinese(String strName) {
        if (!TextUtils.isEmpty(strName)) {
            char[] ch = strName.toCharArray();
            for (char c : ch) {
                if (!isChinese(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public static boolean isPhoneNumber(@NonNull String phone) {
        Matcher matcher = VALID_PHONE_REGEX .matcher(phone);
        return matcher.find();
    }

    public static int parseInteger(String intString, int defaultValue) {
        int number;
        try {
            number = TextUtils.isEmpty(intString) ? defaultValue : Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            number = defaultValue;
        }
        return number;
    }

    public static long parseLong(String intString, long defaultValue) {
        long number;
        try {
            number = TextUtils.isEmpty(intString) ? defaultValue : Long.parseLong(intString);
        } catch (NumberFormatException e) {
            number = defaultValue;
        }
        return number;
    }

    public static double parseDouble(String intString, double defaultValue) {
        double number;
        try {
            number = TextUtils.isEmpty(intString) ? defaultValue : Double.parseDouble(intString);
        } catch (NumberFormatException e) {
            number = defaultValue;
        }
        return number;
    }

    public static String formatString(@StringRes int stringRes, Object ...args) {
        return String.format(PalmUtils.getStringCompact(stringRes), args);
    }
}
