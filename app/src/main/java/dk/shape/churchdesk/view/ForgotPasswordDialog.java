package dk.shape.churchdesk.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dk.shape.churchdesk.R;

//
// ForgotPasswordDialog
// churchdesk-android
//
// Created by davidjorgensen on 16-06-2015.
// Copyright (c) 2015 SHAPE A/S. All rights reserved.
// 
public class ForgotPasswordDialog extends Dialog {

    public interface ForgotPasswordListener {
        void onForgotPasswordClicked(String email);
    }

    @InjectView(R.id.edit_email)
    protected EditText edit_email;

    private ForgotPasswordListener _listener;

    public ForgotPasswordDialog(Context context, ForgotPasswordListener listener) {
        super(context);
        _listener = listener;
        setContentView(R.layout.dialog_forgot_password);
        ButterKnife.inject(this);
        setTitle(R.string.dialog_forgot_password_title);
    }

    @OnClick(R.id.dialog_cancel)
    protected void onCancelClicked() {
        dismiss();
    }

    @OnClick(R.id.dialog_accept)
    protected void onAcceptClicked() {
        if(edit_email.getText().length() > 0) {
            _listener.onForgotPasswordClicked(edit_email.getText().toString());
        }

        dismiss();
    }

}