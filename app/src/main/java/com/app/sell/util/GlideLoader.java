package com.app.sell.util;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideLoader {

    public static void loadIfValid(final Context context, final String uri, final ImageView imageView) {
        if (isValidContextForGlide(context)) {
            Glide.with(context).load(uri).into(imageView);
        }
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}
