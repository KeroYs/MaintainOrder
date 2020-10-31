package com.github.multidestroy.i18n;

import com.github.multidestroy.environment.TimeArgument;
import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;

public class SpecialTypeInfo extends HashMap<String, String> {

    @AllArgsConstructor
    public enum ArgType {
        RECEIVER("receiver"),
        GIVER("giver"),
        TIME("time"),
        REASON("reason"),
        LEFT_TIME("left_time"),
        EXPIRATION_DATE("expiration_date");

        @Getter
        private final String arg;
    }

    public SpecialTypeInfo(String receiver, String giver, String time, String reason) {
        setValue(ArgType.RECEIVER, receiver);
        setValue(ArgType.GIVER, giver);
        setValue(ArgType.TIME, time);
        setValue(ArgType.REASON, reason);
    }

    public void setValue(ArgType argType, String value) {
        put(argType.getArg(), value);
    }

    public String getValue(ArgType argType) {
        return getOrDefault(argType.getArg(), "");
    }

}
