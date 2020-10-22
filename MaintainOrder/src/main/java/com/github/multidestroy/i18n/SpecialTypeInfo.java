package com.github.multidestroy.i18n;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SpecialTypeInfo {

    @Setter @Getter
    private String receiver;
    @Setter @Getter
    private String giver;
    @Setter @Getter
    private String time;
    @Setter @Getter
    private String reason;
    @Setter @Getter
    private String leftTime;
    @Setter @Getter
    private String expirationDate;

    public SpecialTypeInfo() {
        receiver = "";
        giver = "";
        time = "";
        reason = "";
        leftTime = "";
        expirationDate = "";
    }

}