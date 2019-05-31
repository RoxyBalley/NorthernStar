package com.roxy.northernstar;

import java.math.BigDecimal;

public class Utils {
    public static Double roundNumber(Double d, int decimalPlace) {
        try {
            BigDecimal bd = new BigDecimal(Double.toString(d));
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bd.doubleValue();
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0.0;
    }
}
