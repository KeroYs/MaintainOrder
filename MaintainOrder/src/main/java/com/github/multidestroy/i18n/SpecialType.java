package com.github.multidestroy.i18n;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

public enum SpecialType {
    COMMAND_BAN_RECEIVER(true, true, true, true),
    COMMAND_BAN_GLOBAL(false, true, true, true),
    COMMAND_GBAN_RECEIVER(true, true, true, true),
    COMMAND_GBAN_GLOBAL(false, true, true, true),
    COMMAND_MUTE_GLOBAL(true, true, true, true),
    COMMAND_MUTECHAT_ON(false, true, false, false),
    COMMAND_MUTECHAT_OFF(false, true, false, false),
    COMMAND_KICK_RECEIVER(false, true, false, true),
    COMMAND_KICK_GLOBAL(true, true, false, true),

    EVENT_BAN(false, true, false, true, true, true),
    EVENT_GBAN(false, true, false, true, true, true),
    EVENT_MUTE(false, false, false, false, true, false),
    EVENT_CHATOFF(false, false, false, false, false, false);

    private final boolean receiver;
    private final boolean giver;
    private final boolean time;
    private final boolean reason;
    private final boolean leftTime;
    private final boolean expirationDate;

    SpecialType(boolean receiver, boolean giver, boolean time, boolean reason) {
        this.receiver = receiver;
        this.giver = giver;
        this.time = time;
        this.reason = reason;
        this.leftTime = false;
        this.expirationDate = false;
    }

    SpecialType(boolean receiver, boolean giver, boolean time, boolean reason, boolean leftTime, boolean expirationDate) {
        this.receiver = receiver;
        this.giver = giver;
        this.time = time;
        this.reason = reason;
        this.leftTime = leftTime;
        this.expirationDate = expirationDate;
    }

    public String getString(ResourceBundle resourceBundle, SpecialTypeInfo specialTypeInfo) {
        return replaceSpecialType(
                getStringFromResourceBundle(resourceBundle),
                specialTypeInfo
        );
    }

    private String getStringFromResourceBundle(ResourceBundle resourceBundle) {
        switch (this) {
            case COMMAND_BAN_RECEIVER:
                return resourceBundle.getString("SPECIAL.COMMAND.BAN.RECEIVER");
            case COMMAND_BAN_GLOBAL:
                return resourceBundle.getString("SPECIAL.COMMAND.BAN.GLOBAL");
            case COMMAND_GBAN_RECEIVER:
                return resourceBundle.getString("SPECIAL.COMMAND.GBAN.RECEIVER");
            case COMMAND_GBAN_GLOBAL:
                return resourceBundle.getString("SPECIAL.COMMAND.GBAN.GLOBAL");
            case COMMAND_MUTE_GLOBAL:
                return resourceBundle.getString("SPECIAL.COMMAND.MUTE.GLOBAL");
            case COMMAND_MUTECHAT_ON:
                return resourceBundle.getString("SPECIAL.COMMAND.MUTECHAT.ON");
            case COMMAND_MUTECHAT_OFF:
                return resourceBundle.getString("SPECIAL.COMMAND.MUTECHAT.OFF");
            case COMMAND_KICK_RECEIVER:
                return resourceBundle.getString("SPECIAL.COMMAND.KICK/GKICK.RECEIVER");
            case COMMAND_KICK_GLOBAL:
                return resourceBundle.getString("SPECIAL.COMMAND.KICK/GKICK.GLOBAL");
            case EVENT_BAN:
                return resourceBundle.getString("SPECIAL.EVENT.BAN");
            case EVENT_GBAN:
                return resourceBundle.getString("SPECIAL.EVENT.GBAN");
            case EVENT_MUTE:
                return resourceBundle.getString("SPECIAL.EVENT.MUTE");
            case EVENT_CHATOFF:
                return resourceBundle.getString("SPECIAL.EVENT.CHATOFF");
        }
        return "";
    }

    private String replaceSpecialType(String text, SpecialTypeInfo specialTypeInfo) {
        if (receiver)
            text = text.replaceAll("(?i)" + Pattern.quote("<Receiver>"), specialTypeInfo.getReceiver());
        if (giver)
            text = text.replaceAll("(?i)" + Pattern.quote("<Giver>"), specialTypeInfo.getGiver());
        if (time)
            text = text.replaceAll("(?i)" + Pattern.quote("<Time>"), specialTypeInfo.getTime());
        if (reason)
            text = text.replaceAll("(?i)" + Pattern.quote("<Reason>"), specialTypeInfo.getReason());
        if (leftTime)
            text = text.replaceAll("(?i)" + Pattern.quote("<Left_time>"), specialTypeInfo.getLeftTime());
        if (expirationDate)
            text = text.replaceAll("(?i)" + Pattern.quote("<Expiration_date>"), specialTypeInfo.getExpirationDate());

        return text;
    }

}
