package com.company;

import java.util.HashMap;
import java.util.Map;

//This class helps us to convert host's names in Greek to Greeklish, to prevent errors.
//If function's input is an english word, it returns without any change.

public class GreekToGreeklish {
    public static String convert(String name) {

        //Takes a word and matches every greek letter with the corresponding english letter.
        String greeklish = name.toLowerCase();
        StringBuilder sb = new StringBuilder();
        Map<String, String> values = new HashMap<String, String>();

        values.put("α", "a");
        values.put("ά", "a");
        values.put("β", "b");
        values.put("γ", "g");
        values.put("δ", "d");
        values.put("ε", "e");
        values.put("έ", "e");
        values.put("ζ", "z");
        values.put("η", "i");
        values.put("ή", "i");
        values.put("θ", "th");
        values.put("ι", "i");
        values.put("ί", "i");
        values.put("ϊ", "i");
        values.put("κ", "k");
        values.put("λ", "l");
        values.put("μ", "m");
        values.put("ν", "n");
        values.put("ξ", "ks");
        values.put("ο", "o");
        values.put("ό", "o");
        values.put("π", "p");
        values.put("ρ", "r");
        values.put("σ", "s");
        values.put("ς", "s");
        values.put("τ", "t");
        values.put("υ", "y");
        values.put("ύ", "y");
        values.put("φ", "f");
        values.put("χ", "x");
        values.put("ψ", "ps");
        values.put("ω", "w");
        values.put("ώ", "w");
        values.put("", "");


        for (int i = 0; i <greeklish.length() ; i++) {
            if( values.containsKey( String.valueOf(greeklish.charAt(i))  )){
                sb.append( values.get( String.valueOf(greeklish.charAt(i))  ) );
            }else {
                sb.append( greeklish.charAt(i) );
            }
        }
        return toTitleCase(sb.toString());
    }

    //A function that makes the first letter of each word in a string, uppercase.
    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}