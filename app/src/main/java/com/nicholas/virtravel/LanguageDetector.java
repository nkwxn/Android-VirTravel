package com.nicholas.virtravel;

public class LanguageDetector {

    public enum Language {Korean, Japanese, English, Chinese}

    public boolean isEnglish(CharSequence charSequence) {
        boolean isEnglish = true;
        for (char c : charSequence.toString().toCharArray()) {
            if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                isEnglish = false;
                break;
            }
        }

        return isEnglish;
    }

    public boolean hasKorean(CharSequence charSequence) {
        boolean hasKorean = false;
        for (char c : charSequence.toString().toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES) {
                hasKorean = true;
                break;
            }
        }

        return hasKorean;
    }

    public boolean hasJapanese(CharSequence charSequence) {
        boolean hasJapanese = false;
        for (char c : charSequence.toString().toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HIRAGANA
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
                hasJapanese = true;
                break;
            }
        }

        return hasJapanese;
    }

    public boolean hasChinese(String charSequence) {
        boolean hasChinese = false;
        if (charSequence.matches("[\\u4E00-\\u9FA5]+")) {
            System.out.println("is Chinese");
        }
        return hasChinese;
    }
}
