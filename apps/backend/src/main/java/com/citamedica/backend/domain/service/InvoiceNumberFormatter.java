package com.citamedica.backend.domain.service;

import java.util.regex.Pattern;

public final class InvoiceNumberFormatter {

    private static final Pattern SANITIZE = Pattern.compile("[^A-Z0-9]");

    private InvoiceNumberFormatter() {}

    public static String format(String clinicSlug, int year, int sequence) {
        String upper = clinicSlug != null ? clinicSlug.toUpperCase() : "CLINIC";
        String prefix = SANITIZE.matcher(upper).replaceAll("");
        if (prefix.length() > 16) {
            prefix = prefix.substring(0, 16);
        }
        if (prefix.isEmpty()) {
            prefix = "CLINIC";
        }
        return prefix + "-" + year + "-" + String.format("%05d", sequence);
    }
}
