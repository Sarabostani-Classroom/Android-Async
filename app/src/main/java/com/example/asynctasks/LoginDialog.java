package com.example.asynctasks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Base64;

public class LoginDialog extends DialogFragment {
    private EditText userName;
    private EditText password;

    LoginDialogListener listener;

    public static interface LoginDialogListener {
        void storeToken(String token);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (LoginDialogListener) context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Login");
        View dialogView = inflater.inflate(R.layout.login_dialog, null);
        builder.setView(dialogView);

        this.userName = dialogView.findViewById(R.id.username_input);
        this.password = dialogView.findViewById(R.id.password_input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            Log.i("INFO", "Positive pressed");
            String username = this.userName.getText().toString();
            String pwd = this.password.getText().toString();
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) return;
            // call a service to login and get a token back
            // for now we mock the token
            String base64 = Base64.getEncoder().encodeToString(String.format("%s%s", username, pwd).getBytes());
            Log.i("INFO", "Token in dialog: " + base64);
            listener.storeToken(base64);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Log.i("INFO", "Negative pressed");
        });

        return builder.create();
    }
}
