package thundrware.com.aurora.helpers;

import android.graphics.Typeface;

import thundrware.com.aurora.MyApplication;

public class Typefaces {

    public static Typeface getTypeface(FONTS fonts) {
        return Typeface.createFromAsset(MyApplication.getContext().getAssets(), "fonts/" + fonts.toString() + ".otf");
    }
}
