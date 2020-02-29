package br.com.pearls.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RemoveDiacritics {

    private static final Map<String, Pattern> MAP = new HashMap<>();
    private static final String map_a = "[áâãàäåāăą]";
    private static final String map_A = "[ÁÂÃÀÄÅĀĂĄ]";
    private static final String map_ae = "æ";
    private static final String map_AE = "Æ";
    private static final String map_e = "[éêèëēėęěĕə]";
    private static final String map_E = "[ÉÊÈËĒĖĘĚĔƏ]";
    private static final String map_i = "[íìîïīį]";
    private static final String map_I = "[ÍÌÎÏĪĮ]";
    private static final String map_oe = "œ";
    private static final String map_OE = "Œ";
    private static final String map_o = "[óôõòöøő]";
    private static final String map_O = "[ÓÔÕÒÖØŐ]";
    private static final String map_u = "[úüùûūůűų]";
    private static final String map_U = "[ÚÜÙÛŪŮŰŲ]";
    private static final String map_c = "[çćč]";
    private static final String map_C = "[ÇĆČ]";
    private static final String map_n = "[ñńņň]";
    private static final String map_N = "[ÑŃŅŇ]";
    private static final String map_s = "[śšş]";
    private static final String map_S = "[ŚŠŞ]";
    private static final String map_ss = "[ßß§]";
    private static final String map_l = "[ĺļľł]";
    private static final String map_L = "[ĹĻĽŁ]";
    private static final String map_r = "[ŕř]";
    private static final String map_R = "[ŔŘ]";
    private static final String map_z = "[źżž]";
    private static final String map_Z = "[ŹŻŽ]";
    private static final String map_d = "[ďđ]";
    private static final String map_D = "[ĎĐ]";
    private static final String map_k = "ķ";
    private static final String map_K = "Ķ";
    private static final String map_y = "ý";
    private static final String map_Y = "Ý";
    private static final String map_g = "[ģğ]";
    private static final String map_G = "[ĢĞ]";
    private static final String map_t = "[þťțţ]";
    private static final String map_T = "[ÞŤȚŢ]";

    public RemoveDiacritics() {
        MAP.put("a", Pattern.compile(map_a));
        MAP.put("A", Pattern.compile(map_A));
        MAP.put("e", Pattern.compile(map_e));
        MAP.put("ae", Pattern.compile(map_ae));
        MAP.put("AE", Pattern.compile(map_AE));
        MAP.put("E", Pattern.compile(map_E));
        MAP.put("i", Pattern.compile(map_i));
        MAP.put("I", Pattern.compile(map_I));
        MAP.put("oe", Pattern.compile(map_oe));
        MAP.put("OE", Pattern.compile(map_OE));
        MAP.put("o", Pattern.compile(map_o));
        MAP.put("O", Pattern.compile(map_O));
        MAP.put("u", Pattern.compile(map_u));
        MAP.put("U", Pattern.compile(map_U));
        MAP.put("c", Pattern.compile(map_c));
        MAP.put("C", Pattern.compile(map_C));
        MAP.put("n", Pattern.compile(map_n));
        MAP.put("N", Pattern.compile(map_N));
        MAP.put("s", Pattern.compile(map_s));
        MAP.put("S", Pattern.compile(map_S));
        MAP.put("ss", Pattern.compile(map_ss));
        MAP.put("l", Pattern.compile(map_l));
        MAP.put("L", Pattern.compile(map_L));
        MAP.put("r", Pattern.compile(map_r));
        MAP.put("R", Pattern.compile(map_R));
        MAP.put("z", Pattern.compile(map_z));
        MAP.put("Z", Pattern.compile(map_Z));
        MAP.put("d", Pattern.compile(map_d));
        MAP.put("D", Pattern.compile(map_D));
        MAP.put("k", Pattern.compile(map_k));
        MAP.put("K", Pattern.compile(map_K));
        MAP.put("y", Pattern.compile(map_y));
        MAP.put("Y", Pattern.compile(map_Y));
        MAP.put("g", Pattern.compile(map_g));
        MAP.put("G", Pattern.compile(map_G));
        MAP.put("t", Pattern.compile(map_t));
        MAP.put("T", Pattern.compile(map_T));
    }

    public static String removeDiacritics(String string) {
        for(Map.Entry<String, Pattern> entry : MAP.entrySet()) {
            Matcher m = entry.getValue().matcher(string);
            string = m.replaceAll(entry.getKey());
        }
        return string;
    }

}
