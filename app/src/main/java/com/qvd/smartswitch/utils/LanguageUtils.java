package com.qvd.smartswitch.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

import static com.qvd.smartswitch.utils.SharedPreferencesUtil.KEY_APP_LANGUAGE;

public class LanguageUtils {
    //更改App语言
    public static void changeAppLanguage(Context context, Locale locale, boolean persistence) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, metrics);
        if (persistence) {
            saveLanguageSetting(context, locale);
        }
    }


    public static boolean isSameWithSetting(Context context) {
        String lang = context.getResources().getConfiguration().locale.getLanguage();
        return !lang.equals(getAppLanguage(context));
    }


    //App 语言持久化

    public static void saveLanguageSetting(Context context, Locale locale) {

        SharedPreferencesUtil.putString(context, KEY_APP_LANGUAGE, locale.getLanguage());

    }

    private static String getAppLanguage(Context context) {
        return SharedPreferencesUtil.getString(context, KEY_APP_LANGUAGE, Locale.getDefault().getLanguage());
    }


    public static Locale getAppLocale(Context context) {
        String lang = (String) SharedPreferencesUtil.getString(context, KEY_APP_LANGUAGE, Locale.getDefault().getLanguage());
        if (!lang.equals(Locale.SIMPLIFIED_CHINESE.getLanguage()) && !lang.equals(Locale.ENGLISH.getLanguage())) {
            lang = Locale.SIMPLIFIED_CHINESE.getLanguage();
        }
        return new Locale(lang);
    }
}
