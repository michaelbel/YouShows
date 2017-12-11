package org.michaelbel.util;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import org.michaelbel.seriespicker.AppLoader;
import org.michaelbel.seriespicker.R;

@SuppressWarnings("all")
public class ThemeUtils {

    public static int selectableItemBackground() {
        int[] attrs = new int[] {
                R.attr.selectableItemBackground
        };

        TypedArray typedArray = AppLoader.AppContext.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        return backgroundResource;
    }

    public static int selectableItemBackgroundBorderless() {
        int[] attrs = new int[] {
                R.attr.selectableItemBackgroundBorderless
        };

        TypedArray typedArray = AppLoader.AppContext.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        return backgroundResource;
    }

    public static Drawable selectableItemBackgroundDrawable() {
        int[] attrs = new int[] {
                android.R.attr.selectableItemBackground
        };

        TypedArray typedArray = AppLoader.AppContext.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = typedArray.getDrawable(0);
        typedArray.recycle();

        return drawableFromTheme;
    }

    public static Drawable selectableItemBackgroundBorderlessDrawable() {
        int[] attrs = new int[] {
                android.R.attr.selectableItemBackgroundBorderless
        };

        TypedArray typedArray = AppLoader.AppContext.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = typedArray.getDrawable(0);
        typedArray.recycle();

        return drawableFromTheme;
    }
}