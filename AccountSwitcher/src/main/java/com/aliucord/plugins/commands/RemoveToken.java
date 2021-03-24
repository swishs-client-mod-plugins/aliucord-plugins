package com.aliucord.plugins.commands;

import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.plugins.AccountSwitcher;

import java.util.HashMap;
import java.util.Map;

public class RemoveToken {
    public static CommandsAPI.CommandResult execute(Map<String, ?> args, SettingsAPI sets, AccountSwitcher Main) {
        HashMap<String, String> settings = sets.getObject("tokens", new HashMap<>(), AccountSwitcher.settingsType);
        String name = (String) args.get("name");
        String returnMessage;

        if (name == null || name.equals("")) {
            returnMessage = "Nice try, but the name can not be empty.";
        } else if (!settings.containsKey((name))) {
            returnMessage = "Found no token by the name of \"" + name + "\"";
        } else {
            settings.remove(name);
            sets.setObject("tokens", settings);
            Main.removeChoice(name);
            returnMessage = "Successfully removed token \"" + name + "\"";
        }

        return new CommandsAPI.CommandResult(returnMessage, null, false);
    }
}