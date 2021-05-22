package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PrePatchRes;
import com.aliucord.Utils;

import com.discord.widgets.user.calls.PrivateCallLauncher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CallConfirmationDialog extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        return new Manifest() {{
            authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
            description = "Adds a confirmation modal to launching private calls to prevent accidental calls.";
            version = "1.0.0";
            updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json";
        }};
    }

    private static final String privateCallLauncherClass = "com.discord.widgets.user.calls.PrivateCallLauncher";
    public static Map<String, List<String>> getClassesToPatch() {
        return new HashMap<String, List<String>>() {{
            put(privateCallLauncherClass, Arrays.asList("launchVoiceCall", "launchVideoCall"));
        }};
    }

    @Override
    public void start(Context context) {
        patcher.prePatch(privateCallLauncherClass, "launchVoiceCall", (_this, args) -> {
           CallDialog callDialog = new CallDialog();
           callDialog.passCallUser((PrivateCallLauncher) _this, false, (long) args.get(0));
           callDialog.show(Utils.appActivity.getSupportFragmentManager(), "CallDialog");

           return new PrePatchRes(null);
        });

        patcher.prePatch(privateCallLauncherClass, "launchVideoCall", (_this, args) -> {
            CallDialog callDialog = new CallDialog();
            callDialog.passCallUser((PrivateCallLauncher) _this, true, (long) args.get(0));
            callDialog.show(Utils.appActivity.getSupportFragmentManager(), "CallDialog");

            return new PrePatchRes(null);
        });
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}