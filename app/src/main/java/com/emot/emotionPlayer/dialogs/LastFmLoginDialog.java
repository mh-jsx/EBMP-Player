package com.emot.emotionPlayer.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.emot.emotionPlayer.R;
import com.emot.emotionPlayer.fragments.SettingsFragment;
import com.emot.emotionPlayer.lastfmapi.LastFmClient;
import com.emot.emotionPlayer.lastfmapi.callbacks.UserListener;
import com.emot.emotionPlayer.lastfmapi.models.UserLoginQuery;


public class LastFmLoginDialog extends DialogFragment {
    public static final String FRAGMENT_NAME = "LastFMLogin";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity()).
                positiveText("Login").
                negativeText(getString(R.string.cancel)).
                title(getString(R.string.lastfm_login)).
                customView(R.layout.dialog_lastfm_login, false).
                onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String username = ((EditText) dialog.findViewById(R.id.lastfm_username)).getText().toString();
                        String password = ((EditText) dialog.findViewById(R.id.lastfm_password)).getText().toString();
                        if (username.length() == 0 || password.length() == 0) return;
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Logging in..");
                        progressDialog.show();
                        LastFmClient.getInstance(getActivity()).getUserLoginInfo(new UserLoginQuery(username, password), new UserListener() {

                            @Override
                            public void userSuccess() {
                                progressDialog.dismiss();
                                if (getTargetFragment() instanceof SettingsFragment) {
                                    ((SettingsFragment) getTargetFragment()).updateLastFM();
                                }
                            }

                            @Override
                            public void userInfoFailed() {
                                progressDialog.dismiss();
                                Toast.makeText(getTargetFragment().getActivity(), getString(R.string.lastfm_login_failture), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).build();
    }
}
