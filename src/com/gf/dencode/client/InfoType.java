package com.gf.dencode.client;

public final class InfoType {
    public static final String PHONE = "phone";

    private InfoType() {
    }

    static void check(String type) throws PandoraException {
        if (!type.trim().equals(InfoType.PHONE)) {
            throw new PandoraException("Invalid InfoType " + type);
        }
    }
}
