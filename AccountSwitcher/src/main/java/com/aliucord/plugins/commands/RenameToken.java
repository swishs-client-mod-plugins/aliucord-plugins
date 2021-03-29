package com.aliucord.plugins.commands;

import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.plugins.AccountSwitcher;

import java.util.HashMap;
import java.util.Map;

public class RenameToken {
    public static CommandsAPI.CommandResult execute(Map<String, ?> args, SettingsAPI sets, AccountSwitcher Main) {
        HashMap<String, String> settings = sets.getObject("tokens", new HashMap<>(), AccountSwitcher.settingsType);
        String name = (String) args.get("name");
        String rename = (String) args.get("newName");
        String returnMessage;

        if (name == null || name.equals("")) {
            returnMessage = "Nice try, but the name can not be empty.";
        } else if (!settings.containsKey((name))) {
            returnMessage = "I couldn't find the name \"" + name + "\" saved anywhere!";
        } else if (name.equals(rename)) {
            returnMessage = "You can't rename it to itself silly!";
        } else {
            String token = settings.get(name);
            settings.remove(name);
            settings.put(rename, token);
            sets.setObject("tokens", settings);
            Main.removeChoice(name);
            Main.addChoice(rename);
            returnMessage = "The name \"" + name + "\" was successfully renamed to \"" + rename + "\"";
        }

        return new CommandsAPI.CommandResult(returnMessage, null, false);
    }
}