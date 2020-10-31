package com.github.multidestroy.environment;

import com.github.multidestroy.exceptions.WrongArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeArgument {

    @AllArgsConstructor
    private enum TimeArgumentUnit {
        MINUTES(1, 60, ChronoUnit.MINUTES),
        HOURS(1, 24, ChronoUnit.HOURS),
        DAYS(1, 365, ChronoUnit.DAYS);

        private final int minValue;
        private final int maxValue;
        @Getter
        private final ChronoUnit chronoUnit;

        public static TimeArgumentUnit getTimeArgumentUnit(char unit) {
            unit = Character.toLowerCase(unit);

            switch (unit) {
                case 'm':
                    return MINUTES;

                case 'h':
                    return HOURS;

                case 'd':
                    return DAYS;
            }
            throw new RuntimeException();
        }

        public static boolean isDefiningTimeArgumentUnit(char unit, int value) {
            unit = Character.toLowerCase(unit);
            if (value < 1)
                return false;

            if (unit == 'm' && value <= 60)
                return true;

            if (unit == 'h' && value <= 24)
                return true;

            return unit == 'd' && value <= 365;
        }
    }

    private final String original;
    private final TimeArgumentUnit timeArgumentUnit;
    private final int value;

    public TimeArgument(String time, boolean daysAllowed) throws WrongArgumentException {
        this.original = time;
        if (isGoodStructure(time)) {
            this.timeArgumentUnit = getTimeArgumentUnit(time);

            if (!daysAllowed && timeArgumentUnit == TimeArgumentUnit.DAYS)
                throw new WrongArgumentException(ChatColor.RED + "Wrong time argument! Correct values: ([1-60]m, [1-24]h)");

            this.value = getValue(time);
        } else {
            if (daysAllowed)
                throw new WrongArgumentException(ChatColor.RED + "Wrong time argument! Correct values: ([1-60]m, [1-24]h, [1-365]d)");
            else
                throw new WrongArgumentException(ChatColor.RED + "Wrong time argument! Correct values: ([1-60]m, [1-24]h)");
        }
    }

    public Instant getFutureTime() {
        return Instant.now().plus(value, timeArgumentUnit.getChronoUnit());
    }

    private TimeArgumentUnit getTimeArgumentUnit(String time) {
        return TimeArgumentUnit.getTimeArgumentUnit(time.charAt(time.length() - 1));
    }

    private int getValue(String time) {
        return Integer.parseInt(time.substring(0, time.length() - 1));
    }

    private boolean isGoodStructure(String time) {
        if (time != null && time.length() != 0) {
            try {
                return TimeArgumentUnit.isDefiningTimeArgumentUnit(
                        time.charAt(time.length() - 1),
                        Integer.parseInt(time.substring(0, time.length() - 1)));
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return original;
    }
}
