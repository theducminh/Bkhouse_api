package com.api.bkhouse.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.api.bkhouse.payload.dto.DistrictDTO;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Util {
    private static final String HMAC_SHA512 = "HmacSHA512";

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static String getRandom8Number() {
        return Integer.toString((int) (10000000 + (new Random()).nextFloat()*90000000));
    }

    public static String hmacSHA512(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha512Hmac;
        String result;
        final byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
        sha512Hmac = Mac.getInstance(HMAC_SHA512);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
        sha512Hmac.init(keySpec);
        byte[] macData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        result = bytesToHex(macData);
        return result;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : bytes) {
            int intVal = 0xff & hashByte;
            if (intVal < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(intVal));
        }
        return sb.toString();
    }

    public static int calculatePostPrice(Integer priority, Integer period, boolean sell) {
        float heSo = 0.0f;
        int price = 0;
        if (sell) {
            if (priority == 1) {
                price = 50000;
            } else if (priority == 2) {
                price = 100000;
            } else if (priority == 3) {
                price = 150000;
            }
        } else {
            if (priority == 1) {
                price = 20000;
            } else if (priority == 2) {
                price = 40000;
            } else if (priority == 3) {
                price = 60000;
            }
        }
        if (period == 15) {
            heSo = 1.5f;
        } else if (period == 30) {
            heSo = 3;
        } else if (period == 60) {
            heSo = 6;
        }
        return Math.round(heSo * price);
    }

    public static int agencyMonthlyPaid(List<DistrictDTO> districtDTOS) {
        int total = 0;
        for (DistrictDTO districtDTO: districtDTOS) {
            if (districtDTO.getAdministrativeUnitId() == 5) {
                total += 30000;
            } else {
                total += 20000;
            }
        }
        return total;
    }

    public static int getCurrMonth(Integer nam) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currMonth;
        if (calendar.get(Calendar.YEAR) == nam) {
            currMonth = calendar.get(Calendar.MONTH);
            currMonth++;
        } else {
            currMonth = 12;
        }
        return currMonth;
    }

    public static int getDayOfMonth(Integer month, Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currMonth = calendar.get(Calendar.MONTH);
        currMonth++;
        int currYear = calendar.get(Calendar.YEAR);
        int date;
        if (year == currYear && month == currMonth) {
            date = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                date = 31;
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                date = 30;
            } else {
                if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                    date = 29;
                } else {
                    date = 28;
                }
            }
        }
        return date;
    }

    public static Instant getCurrentDateTime() {
        return ZonedDateTime.now(ZoneId.of("UTC+07:00")).toLocalDateTime().toInstant(ZoneOffset.UTC);
    }
}
