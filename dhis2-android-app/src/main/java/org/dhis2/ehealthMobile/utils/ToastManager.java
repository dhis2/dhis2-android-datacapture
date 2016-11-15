package org.dhis2.ehealthMobile.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.dhis2.ehealthMobile.R;

public final class ToastManager {
    private ToastManager() { }

    public static Toast makeToast(Context context, String message, int duration) {
        Preconditions.checkNotNull(context, "context cannot be null");
        Preconditions.checkNotNull(message, "message cannot be null");

        View root = LayoutInflater.from(context).inflate(R.layout.toast_message_layout, null);
        TextView textView = (TextView) root.findViewById(R.id.toast_text_view);
        textView.setText(message);

        Toast toast = new Toast(context);
        toast.setView(root);
        toast.setDuration(duration);

        return toast;
    }
}
