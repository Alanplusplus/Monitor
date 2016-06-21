package com.os.logger.track;

import android.text.TextUtils;

/**
 * Created by Alan on 16/6/12.
 */
public class PhoneNumber {
    private static String incomingNumber;
    private static String outgoingNumber;

    public static String getIncomingNumber() {
        return incomingNumber;
    }

    public static void setIncomingNumber(String incomingNumber) {
        PhoneNumber.incomingNumber = incomingNumber;
        if (!TextUtils.isEmpty(incomingNumber)){
            PhoneNumber.outgoingNumber = null;
        }
    }

    public static String getOutgoingNumber() {
        return outgoingNumber;
    }

    public static void setOutgoingNumber(String outgoingNumber) {
        PhoneNumber.outgoingNumber = outgoingNumber;
        if (!TextUtils.isEmpty(outgoingNumber)) {
            PhoneNumber.incomingNumber = null;
        }
    }

    public static String getNumber(){
        if (TextUtils.isEmpty(incomingNumber)){
            return outgoingNumber;
        }
        return incomingNumber;
    }

    public static int getType(){
        if (!TextUtils.isEmpty(incomingNumber)){
            return 1;
        }
        return 2;
    }
}
