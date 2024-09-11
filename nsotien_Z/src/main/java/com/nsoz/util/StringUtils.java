
package com.nsoz.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joor.Reflect;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 *
 * @author Admin
 */
public class StringUtils extends com.mysql.cj.util.StringUtils {

    public static String format(String format, Object object) {
        StringBuilder formatter = new StringBuilder(format);
        List<Object> valueList = new ArrayList<Object>();
        Matcher matcher = Pattern.compile("\\$\\{([a-zA-Z0-9.]+)}").matcher(format);
        HashMap<String, Object> values = new HashMap<>();
        while (matcher.find()) {
            String key = matcher.group(1);
            values.put(key, getField(key, object));
            String formatKey = String.format("${%s}", key);
            int index = formatter.indexOf(formatKey);
            if (index != -1) {
                formatter.replace(index, index + formatKey.length(), "%s");
                valueList.add(values.get(key));
            }
        }
        return String.format(formatter.toString(), valueList.toArray());
    }

    private static Object getField(String name, Object obj) {
        String[] array = name.split("\\.");
        Reflect reflect = Reflect.on(obj);
        for (String fieldname : array) {
            reflect = reflect.field(fieldname);
        }
        return reflect.get();
    }

    public static String repeat(char c, int count) {
        char[] arr = new char[count];
        Arrays.fill(arr, c);
        return new String(arr);
    }

    public static String removeAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
    }

    public static boolean checkPassword(String hashed, String plaintext) {
        if (hashed == null || hashed.isEmpty() || plaintext == null || plaintext.isEmpty()) {
            return false;
        }
        return BCrypt.verifyer().verify(plaintext.toCharArray(), hashed).verified;
    }
}
