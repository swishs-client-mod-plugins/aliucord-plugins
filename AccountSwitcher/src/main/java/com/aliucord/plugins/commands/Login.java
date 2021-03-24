package com.aliucord.plugins.commands;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.plugins.AccountSwitcher;
import com.discord.models.domain.auth.ModelLoginResult;
import com.discord.stores.StoreAuthentication;
import com.discord.stores.StoreStream;

import java.util.HashMap;
import java.util.Map;

public class Login {
    public static CommandsAPI.CommandResult execute(Map<String, ?> args, SettingsAPI sets, Context context) {
        HashMap<String, String> settings = sets.getObject("tokens", new HashMap<>(), AccountSwitcher.settingsType);
        String name = (String) args.get("name");
        Boolean restart = (Boolean) args.get("restart");
        String token = settings.get(name);
        String returnMessage;

        if (name == null || name.equals("")) {
            returnMessage = "Nice try, but the name can not be empty.";
        } else if (!settings.containsKey((name))) {
            returnMessage = "Found no token by the name of \"" + name + "\"";
        } else {
            StoreAuthentication.access$dispatchLogin(StoreStream.getAuthentication(), new ModelLoginResult(false, null, token, null));
            // will only return if the app doesn't restart due to Thread.sleep()
            returnMessage = "Hi! It seems you've chosen not to restart... things may be a bit buggy, feel free to quit the app and restart it manually to fully refresh discord!";

            if (restart == null || restart) {
                try { Thread.sleep(500); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                PackageManager packageManager = context.getPackageManager();
                Intent intent = ((PackageManager) packageManager).getLaunchIntentForPackage(context.getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                context.startActivity(mainIntent);
                System.exit(0);
            }
        }

        return new CommandsAPI.CommandResult(returnMessage, null, false);
    }
}
