/*
 * Copyright (c) 2021 Paige
 * Licensed under the GNU General Public License v3.0
 */

package com.aliucord.plugins.commands;

import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.plugins.AccountSwitcher;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddToken {
    public static CommandsAPI.CommandResult execute(Map<String, ?> args, SettingsAPI sets, AccountSwitcher Main) {
        HashMap<String, String> settings = sets.getObject("tokens", new HashMap<>(), AccountSwitcher.settingsType);
        String name = (String) args.get("name");
        String token = (String) args.get("token");
        Matcher nfaToken = Pattern.compile("[\\w-]{24}\\.[\\w-]{6}\\.[\\w-]{27}").matcher(token);
        Matcher mfaToken = Pattern.compile("mfa\\.[\\w-]{84}").matcher(token);
        String returnMessage;

        if (name == null || name.equals("") || token == null || token.equals("")) {
            returnMessage = "Nice try, but the name/token can not be empty.";
        } else if (settings.containsKey((name))) {
            returnMessage = "The name \"" + name + "\" has already been used!";
        } else if (settings.containsValue((token))) {
            returnMessage = "Token ||" + token + "|| is already added!";
        } else if (!nfaToken.matches() && !mfaToken.matches()) {
            returnMessage = "||" + token + "|| is not a valid token!";
        } else {
            settings.put(name, token);
            sets.setObject("tokens", settings);
            Main.addChoice(name);
            returnMessage = "Successfully added token \"" + name + "\"";
        }

        return new CommandsAPI.CommandResult(returnMessage, null, false);
    }
}