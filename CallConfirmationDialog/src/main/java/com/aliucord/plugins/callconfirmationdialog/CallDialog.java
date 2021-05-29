/*
 * Copyright (c) 2021 Paige
 * Licensed under the GNU General Public License v3.0
 */

package com.aliucord.plugins.callconfirmationdialog;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;
import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.aliucord.Utils;

import com.discord.app.AppComponent;
import com.discord.app.AppDialog;
import com.discord.app.AppPermissions;
import com.discord.databinding.LeaveGuildDialogBinding;
import com.discord.views.LoadingButton;
import com.discord.widgets.guilds.leave.WidgetLeaveGuildDialog$binding$2;
import com.discord.widgets.user.calls.PrivateCallLauncher;
import com.discord.widgets.voice.call.PrivateCallLaunchUtilsKt;

import com.google.android.material.button.MaterialButton;

@SuppressWarnings("unused")
public class CallDialog extends AppDialog {
    public CallDialog() {
        super(Utils.getResId("leave_guild_dialog", "layout"));
    }

    private LeaveGuildDialogBinding binding;
    private PrivateCallLauncher callUser;
    private boolean callType;
    private long callArgs;

    @Override
    @SuppressLint("SetTextI18n")
    public void onViewBound(View view) {
        super.onViewBound(view);

        binding = WidgetLeaveGuildDialog$binding$2.INSTANCE.invoke(view);
        LoadingButton confirmButton = getConfirmButton();
        confirmButton.setText("Confirm");
        confirmButton.setIsLoading(false);
        confirmButton.setOnClickListener(e -> {
            AppPermissions.Requests requests = callUser.getAppPermissionsRequests();
            Context context = callUser.getContext();
            AppComponent appComponent = callUser.getAppComponent();
            FragmentManager fragmentManager = callUser.getFragmentManager();

            PrivateCallLaunchUtilsKt.callAndLaunch(callArgs, callType, requests, context, appComponent, fragmentManager);
            dismiss();
        });

        MaterialButton cancelButton = getCancelButton();
        cancelButton.setOnClickListener(e -> dismiss());

        getHeader().setText("Call User?");
        getBody().setText("Are you sure you meant to call this user?");
    }

    public final void passCallUser(PrivateCallLauncher callUser, boolean callType, long callArgs) {
        this.callUser = callUser;
        this.callType = callType;
        this.callArgs = callArgs;
    }

    public final MaterialButton getCancelButton() { return binding.b; }
    public final LoadingButton getConfirmButton() { return binding.c; }
    public final TextView getBody() { return binding.d; }
    public final TextView getHeader() { return binding.e; }
}