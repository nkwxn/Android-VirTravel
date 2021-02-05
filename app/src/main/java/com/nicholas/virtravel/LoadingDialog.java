package com.nicholas.virtravel;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private TextView textView;

    LoadingDialog(Activity myA) {
        activity = myA;
    }

    void startLoadingDialog(String loadingText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_loading, null);
        textView = v.findViewById(R.id.tvLoading);
        textView.setText(loadingText);
        builder.setView(v);
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}
