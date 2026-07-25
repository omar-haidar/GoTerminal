package dev.omar.goterminal.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class UiUtils {

    public static void addSystemWindowInsetToPadding(View view, boolean left, boolean top, boolean right, boolean bottom) {
        addWindowInsetToPadding(view, WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout(), left, top, right, bottom);
    }

    public static void addWindowInsetToPadding(View view, int insetsTypeMask, boolean left, boolean top, boolean right, boolean bottom) {
        int initialLeft = view.getPaddingLeft();
        int initialTop = view.getPaddingTop();
        int initialRight = view.getPaddingRight();
        int initialBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(insetsTypeMask);
            view.setPadding(
                    initialLeft + (left ? insets.left : 0),
                    initialTop + (top ? insets.top : 0),
                    initialRight + (right ? insets.right : 0),
                    initialBottom + (bottom ? insets.bottom : 0)
            );
            return windowInsets;
        });
    }

    public static void addSystemWindowInsetToMargin(View view, boolean left, boolean top, boolean right, boolean bottom) {
        addWindowInsetToMargin(view, WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout(), left, top, right, bottom);
    }

    public static void addWindowInsetToMargin(View view, int insetsTypeMask, boolean left, boolean top, boolean right, boolean bottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int initialLeft = params.leftMargin;
        int initialTop = params.topMargin;
        int initialRight = params.rightMargin;
        int initialBottom = params.bottomMargin;

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(insetsTypeMask);
            params.setMargins(
                    initialLeft + (left ? insets.left : 0),
                    initialTop + (top ? insets.top : 0),
                    initialRight + (right ? insets.right : 0),
                    initialBottom + (bottom ? insets.bottom : 0)
            );
            view.requestLayout();
            return windowInsets;
        });
    }
}
