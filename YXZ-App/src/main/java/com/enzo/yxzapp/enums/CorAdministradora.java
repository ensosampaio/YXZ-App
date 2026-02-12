package com.enzo.yxzapp.enums;

public enum CorAdministradora {
    ROSA("#FFB3D9", "#FFF0F7"),
    AZUL_CEU("#87CEEB", "#E6F7FF"),
    AMBAR("#FFBF00", "#FFF8E1");

    private final String corPrincipal;
    private final String corSuave;

    CorAdministradora(String corPrincipal, String corSuave) {
        this.corPrincipal = corPrincipal;
        this.corSuave = corSuave;
    }

    public String getCorPrincipal() {
        return corPrincipal;
    }

    public String getCorSuave() {
        return corSuave;
    }
}
