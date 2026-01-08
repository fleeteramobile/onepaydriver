package com.onepaytaxi.driver.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.onepaytaxi.driver.R;

import java.lang.reflect.Field;

/**
 * Used to change Font style
 */
public class FontHelper {
    private static final String TAG = FontHelper.class.getSimpleName();
    public final static String FONT_TYPEFACE = "Avenir Book.ttf";

    /**
     * Apply specified font for all text views (including nested ones) in the specified root view.
     */
    public static void applyFont(final Context context, final View root, final String fontPath) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++)
                    applyFont(context, viewGroup.getChildAt(i), fontPath);
            } else if (root instanceof TextView)
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
            else if (root instanceof EditText)
                ((EditText) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void applyFont(final Context context, final View root) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++)
                    applyFont(context, viewGroup.getChildAt(i), FONT_TYPEFACE);
            } else if (root instanceof TextView)
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
            else if (root instanceof EditText)
                ((EditText) root).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
            else if (root instanceof DatePicker || root instanceof TimePicker) {
                overrideFonts(context, root);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertToArabic(int value) {
        String newValue = (((((((((((String.valueOf(value)).replaceAll("1", "١"
        )).replaceAll("2", "٢")).replaceAll("3", "٣")).replaceAll("4", "٤")).replaceAll("5", "٥")).replaceAll("6", "٦")).replaceAll("7", "٧")).replaceAll("8", "٨")).replaceAll("9", "٩")).replaceAll("0", "٠"));
        return newValue;
    }

    //٫
    public static String convertfromArabic(String value) {
        String newValue = ((((((((((((value).replaceAll("١", "1")).replaceAll("٢", "2")).replaceAll("٣", "3")).replaceAll("٤", "4")).replaceAll("٥", "5")).replaceAll("٦", "6")).replaceAll("٧", "7")).replaceAll("٨", "8")).replaceAll("٩", "9")).replaceAll("٠", "0").replaceAll("٫", ".")));
        newValue = newValue.replace(",\"", "*&^");
        newValue = newValue.replace(",", ".");
        newValue = newValue.replace("*&^", ",\"");
        return newValue;
    }


    public static void overrideFonts(Context context, View v) {
        ViewGroup picker;
        try {
            picker = (DatePicker) v;
        } catch (Exception e) {
            picker = (TimePicker) v;
        }
        LinearLayout layout1 = (LinearLayout) picker.getChildAt(0);
        if (picker instanceof TimePicker) {
            if (layout1.getChildAt(1) instanceof NumberPicker) {
                NumberPicker v1 = (NumberPicker) layout1.getChildAt(1);
                final int count = v1.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = v1.getChildAt(i);

                    try {
                        Field wheelpaint_field = v1.getClass().getDeclaredField("mSelectorWheelPaint");
                        wheelpaint_field.setAccessible(true);
                        ((Paint) wheelpaint_field.get(v1)).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        ((Paint) wheelpaint_field.get(v1)).setColor(CL.getResources().getColor(R.color.pure_black));
                        ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        v1.invalidate();
                    } catch (Exception e) {
                        //TODO catch.
                        //If java cant find field then it will catch here and app wont crash.
                    }
                }
            }
            if (layout1.getChildAt(2) instanceof NumberPicker) {
                NumberPicker v1 = (NumberPicker) layout1.getChildAt(1);
                final int count = v1.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = v1.getChildAt(i);

                    try {
                        Field wheelpaint_field = v1.getClass().getDeclaredField("mSelectorWheelPaint");
                        wheelpaint_field.setAccessible(true);
                        ((Paint) wheelpaint_field.get(v1)).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        ((Paint) wheelpaint_field.get(v1)).setColor(CL.getResources().getColor(R.color.pure_black));
                        ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        v1.invalidate();
                    } catch (Exception e) {
                        //TODO catch.
                        //If java cant find field then it will catch here and app wont crash.
                    }
                }
            }
            if (layout1.getChildAt(0) instanceof NumberPicker) {
                NumberPicker v1 = (NumberPicker) layout1.getChildAt(1);
                final int count = v1.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = v1.getChildAt(i);

                    try {
                        Field wheelpaint_field = v1.getClass().getDeclaredField("mSelectorWheelPaint");
                        wheelpaint_field.setAccessible(true);
                        ((Paint) wheelpaint_field.get(v1)).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        ((Paint) wheelpaint_field.get(v1)).setColor(CL.getResources().getColor(R.color.pure_black));
                        ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        v1.invalidate();
                    } catch (Exception e) {
                        //TODO catch.
                        //If java cant find field then it will catch here and app wont crash.
                    }
                }
            }
        }
        ViewGroup layout = (ViewGroup) layout1.getChildAt(0);
        for (int j = 0; j < 3; j++) {
            try {
                if (layout.getChildAt(j) instanceof NumberPicker) {
                    NumberPicker v1 = (NumberPicker) layout.getChildAt(j);
                    final int count = v1.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View child = v1.getChildAt(i);

                        try {
                            Field wheelpaint_field = v1.getClass().getDeclaredField("mSelectorWheelPaint");
                            wheelpaint_field.setAccessible(true);
                            ((Paint) wheelpaint_field.get(v1)).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                            ((Paint) wheelpaint_field.get(v1)).setColor(CL.getResources().getColor(R.color.pure_black));
                            ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                            v1.invalidate();
                        } catch (Exception e) {
                            //TODO catch.
                            //If java cant find field then it will catch here and app wont crash.
                        }
                    }
                }
            } catch (Exception e) {
                //TODO catch.
                //If java cant find field then it will catch here and app wont crash.
            }
        }

    }


}
