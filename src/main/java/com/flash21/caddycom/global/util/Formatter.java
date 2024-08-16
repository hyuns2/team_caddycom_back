package com.flash21.caddycom.global.util;

public class Formatter {
    public static String formatPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("[^0-9]", "");
    }
}
