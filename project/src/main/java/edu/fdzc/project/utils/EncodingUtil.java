package edu.fdzc.project.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class EncodingUtil {

    // Base64编码
    public static String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    // URL编码
    public static String urlEncode(String input) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    // 先Base64编码，再URL编码
    public static String base64AndUrlEncode(String input) throws UnsupportedEncodingException {
        String base64Encoded = base64Encode(input);
        return urlEncode(base64Encoded);
    }
}
