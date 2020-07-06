package com.emot.emotionPlayer.widgets;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.afollestad.appthemeengine.util.TintHelper;

@SuppressLint("AppCompatCustomView")
public class PopupImageView extends ImageView {

    public PopupImageView(Context context) {
        super(context);
        tint();
    }

    public PopupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tint();
    }

    public PopupImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tint();
    }

    @TargetApi(21)
    public PopupImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        tint();
    }

    private void tint() {
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("dark_theme", false)) {
            TintHelper.setTint(this, Color.parseColor("#eeeeee"));
        } else  TintHelper.setTint(this, Color.parseColor("#434343"));
    }

}
