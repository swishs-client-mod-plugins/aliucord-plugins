package com.aliucord.plugins.commands;

import com.aliucord.api.CommandsAPI;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.MessageEmbed;
import com.aliucord.plugins.AccountSwitcher;

import java.util.Collections;
import java.util.HashMap;

public class ListTokens {
    public static CommandsAPI.CommandResult execute(SettingsAPI sets) {
        HashMap<String, String> settings = sets.getObject("tokens", null,  AccountSwitcher.settingsType);

        MessageEmbed embed = new MessageEmbed();
        if (settings == null || settings.isEmpty()) embed.setTitle("You haven't added any tokens yet!");
        else {
            int size = settings.size();
            embed.setTitle("You have " + size + " token" + (size == 1 ? "" : "s") + " currently added:");
            StringBuilder description = new StringBuilder();
            for (String name : settings.keySet()) {
                if (description.length() > 0) description.append("\n");
                description.append(name).append(" : ||").append(settings.get(name)).append("||");
            }
            embed.setDescription(description.toString());
        }

        return new CommandsAPI.CommandResult(null, Collections.singletonList(embed), false);
    }
}