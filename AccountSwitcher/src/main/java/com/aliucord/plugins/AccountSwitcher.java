package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.Utils;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import com.discord.models.commands.ApplicationCommandOption;
import com.aliucord.plugins.commands.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class AccountSwitcher extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
        manifest.description = "Lets you switch between multiple accounts with chat commands.";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json"; // change
        return manifest;
    }

    @Override
    public void start(Context context) {
        commandChoices = new ArrayList<>();
        ApplicationCommandOption requiredNameOption = new ApplicationCommandOption(ApplicationCommandType.STRING, "name", "Token name", null, true, true, null, null);
        ApplicationCommandOption requiredTokenOption = new ApplicationCommandOption(ApplicationCommandType.STRING, "token", "Account Token", null, true, true, null, null);
        ApplicationCommandOption requiredTokenChoice = new ApplicationCommandOption(ApplicationCommandType.STRING, "name", "Token name", null, true, true, commandChoices, null);
        ApplicationCommandOption restartDiscordChoice = new ApplicationCommandOption(ApplicationCommandType.BOOLEAN, "restart", "Restarts discord", null, false, true, null, null);

        List<ApplicationCommandOption> Commands = new ArrayList<ApplicationCommandOption>() {{
            add(new ApplicationCommandOption(
                    ApplicationCommandType.SUBCOMMAND,
                    "list",
                    "Privately sends your added tokens",
                    null, false, false, null,
                    Collections.emptyList()
            ));

            add(new ApplicationCommandOption(
                    ApplicationCommandType.SUBCOMMAND,
                    "add",
                    "Add a token",
                    null,
                    false,
                    false,
                    null,
                    Arrays.asList(requiredNameOption, requiredTokenOption)
            ));

            add(new ApplicationCommandOption(
                    ApplicationCommandType.SUBCOMMAND,
                    "remove",
                    "Remove a token",
                    null,
                    false,
                    false,
                    null,
                    Collections.singletonList(requiredTokenChoice)
            ));

            add(new ApplicationCommandOption(
                    ApplicationCommandType.SUBCOMMAND,
                    "login",
                    "Log into a token",
                    null,
                    false,
                    false,
                    null,
                    Arrays.asList(requiredTokenChoice, restartDiscordChoice)
            ));
        }};

        HashMap<String, String> settings = sets.getObject("tokens", null, settingsType);
        if (settings != null) for (String name : settings.keySet()) addChoice(name);

        commands.registerCommand(
                "token",
                "Login to and manage your saved tokens",
                Commands,
                args -> {
                    if (args.containsKey("list")) return ListTokens.execute(sets);
                    if (args.containsKey("add")) return AddToken.execute((Map<String, ?>) args.get("add"), sets, this);
                    if (args.containsKey("remove")) return RemoveToken.execute((Map<String, ?>) args.get("remove"), sets, this);
                    if (args.containsKey("login")) return Login.execute((Map<String, ?>) args.get("login"), sets, context);

                    return new CommandsAPI.CommandResult("Error! Insufficient arguments and/or no arguments provided.", null ,false);
                }
        );
    }

    @Override
    public void stop(Context context) { commands.unregisterAll(); }
    public static final Type settingsType = TypeToken.getParameterized(HashMap.class, String.class, String.class).getType();
    public List<CommandChoice> commandChoices;

    public void addChoice(String name) {
        commandChoices.add(Utils.createCommandChoice(name, name));
    }

    public void removeChoice(String name) {
        commandChoices.remove(Utils.createCommandChoice(name, name));
    }
}