package com.github.multidestroy.info;

public enum ModificationType {
    EXPIRED((byte) 0),
    UNBAN((byte) 1),
    EDITED((byte) 2);

    private byte value;

    ModificationType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ModificationType getType(byte id) {
        switch (id) {
            case 0:
                return EXPIRED;
            case 1:
                return UNBAN;
            case 2:
                return EDITED;
        }
        return EXPIRED;
    }
}
