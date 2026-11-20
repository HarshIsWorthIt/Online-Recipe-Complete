package com.recipely.util;

/** Minimal output encoder for JSP content rendered from user-provided fields. */
public final class HtmlUtil {
    private HtmlUtil() { }
    public static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
