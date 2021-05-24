/*
 * Copyright (c) 2021 Paige
 * Licensed under the GNU General Public License v3.0
 */

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
    public static CommandsAPI.CommandResult execute(Map<String, ?> args, SettingsAPI sets, AccountSwitcher main, Context context) {
        HashMap<String, String> settings = sets.getObject("tokens", new HashMap<>(), AccountSwitcher.settingsType);
        String name = (String) args.get("name");
        String token = settings.get(name);
        Boolean restart = (Boolean) args.get("restart");
        String returnMessage;

        if (name == null || name.equals("")) {
            returnMessage = "Nice try, but the name can not be empty.";
        } else if (!settings.containsKey((name))) {
            returnMessage = "Found no token by the name of \"" + name + "\"";
        } else {
            main.userSettings = StoreStream.getUserSettings().prefs.getAll();
            if (restart == null || !restart) {
                StoreStream.getAuthentication().setAuthed(null);
                StoreAuthentication.access$dispatchLogin(StoreStream.getAuthentication(), new ModelLoginResult(false, null, token, null));
            } else {
                StoreAuthentication.access$dispatchLogin(StoreStream.getAuthentication(), new ModelLoginResult(false, null, token, null));
                try { Thread.sleep(500); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                PackageManager packageManager = context.getPackageManager();
                Intent intent = ((PackageManager) packageManager).getLaunchIntentForPackage(context.getPackageName());
                ComponentName componentName = intent.getComponent();

                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                context.startActivity(mainIntent);
                System.exit(0);
            }
            returnMessage = "\"" + name + "\" has been logged into successfully.";
        }

        return new CommandsAPI.CommandResult(returnMessage, null, false);
    }
}
