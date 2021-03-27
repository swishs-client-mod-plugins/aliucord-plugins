package com.aliucord.plugins;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.aliucord.Utils;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.commands.CommandChoice;
import com.discord.models.commands.ApplicationCommandOption;
import com.aliucord.plugins.commands.*;
import com.discord.stores.StoreStream;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

@SuppressWarnings("unused")
public class AccountSwitcher extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
        manifest.description = "Lets you switch between multiple accounts with chat commands.";
        manifest.version = "1.0.2";
        manifest.updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    public static Map<String, List<String>> getClassesToPatch() {
        return new HashMap<String, List<String>>() {{
            put("com.discord.stores.StoreAuthentication", Collections.singletonList("handleAuthToken$app_productionDiscordExternalRelease"));
        }};
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void start(Context context) {
        patcher.patch("com.discord.stores.StoreAuthentication", "handleAuthToken$app_productionDiscordExternalRelease", (_this, args, ret) -> {
            if (userSettings != null) {
                userSettings.remove("STORE_AUTHED_TOKEN");
                userSettings.forEach((key, value) -> {
                    if (value instanceof String) StoreStream.getUserSettings().prefs.edit().remove(key).putString(key, (String) value).apply();
                    if (value instanceof Boolean) StoreStream.getUserSettings().prefs.edit().remove(key).putBoolean(key, (Boolean) value).apply();
                    if (value instanceof Integer) StoreStream.getUserSettings().prefs.edit().remove(key).putInt(key, (Integer) value).apply();
                });
            }
            return ret;
        });

        commandChoices = new ArrayList<>();
        ApplicationCommandOption requiredNameOption = new ApplicationCommandOption(ApplicationCommandType.STRING, "name", "Name", null, true, true, null, null);
        ApplicationCommandOption requiredNewNameOption = new ApplicationCommandOption(ApplicationCommandType.STRING, "newName", "New Name", null, true, true, null, null);
        ApplicationCommandOption requiredTokenOption = new ApplicationCommandOption(ApplicationCommandType.STRING, "token", "Account Token (type \"current token\" to use your current token)", null, true, true, null, null);
        ApplicationCommandOption requiredNameChoice = new ApplicationCommandOption(ApplicationCommandType.STRING, "name", "Name", null, true, true, commandChoices, null);
        ApplicationCommandOption restartDiscordChoice = new ApplicationCommandOption(ApplicationCommandType.BOOLEAN, "restart", "Will reload discord when switching accounts (use this if you're having issues)", null, false, true, null, null);

        List<ApplicationCommandOption> Commands = new ArrayList<ApplicationCommandOption>() {{
            add(new ApplicationCommandOption(ApplicationCommandType.SUBCOMMAND, "add", "Add a token", null, false, false, null, Arrays.asList(requiredNameOption, requiredTokenOption)));
            add(new ApplicationCommandOption(ApplicationCommandType.SUBCOMMAND, "remove", "Remove a token", null, false, false, null, Collections.singletonList(requiredNameChoice)));
            add(new ApplicationCommandOption(ApplicationCommandType.SUBCOMMAND, "list", "Lists all tokens", null, false, false, null, Collections.emptyList()));
            add(new ApplicationCommandOption(ApplicationCommandType.SUBCOMMAND, "update", "Update a Token (from its name value)", null, false, false, null, Arrays.asList(requiredNameChoice, requiredTokenOption)));
            add(new ApplicationCommandOption(ApplicationCommandType.SUBCOMMAND, "rename", "Rename a token (from its name value)", null, false, false, null, Arrays.asList(requiredNameChoice, requiredNewNameOption)));
            add(new ApplicationCommandOption(ApplicationCommandType.SUBCOMMAND, "login", "Log into a token", null, false, false, null, Arrays.asList(requiredNameChoice, restartDiscordChoice)));
        }};

        HashMap<String, String> settings = sets.getObject("tokens", null, settingsType);
        if (settings != null) for (String name : settings.keySet()) addChoice(name);

        commands.registerCommand("token", "Log into and manage your saved tokens", Commands, args -> {
                    if (args.containsKey("add")) return AddToken.execute((Map<String, ?>) args.get("add"), sets, this);
                    if (args.containsKey("remove")) return RemoveToken.execute((Map<String, ?>) args.get("remove"), sets, this);
                    if (args.containsKey("list")) return ListTokens.execute(sets);
                    if (args.containsKey("update")) return UpdateToken.execute((Map<String, ?>) args.get("update"), sets);
                    if (args.containsKey("rename")) return RenameToken.execute((Map<String, ?>) args.get("rename"), sets, this);
                    if (args.containsKey("login")) return Login.execute((Map<String, ?>) args.get("login"), sets, this, context);

                    return new CommandsAPI.CommandResult("Error! Insufficient arguments and/or no arguments provided.", null ,false);
                }
        );
    }

    public Map<String, ?> userSettings;
    public List<CommandChoice> commandChoices;
    public static final Type settingsType = TypeToken.getParameterized(HashMap.class, String.class, String.class).getType();
    public void addChoice(String name) { commandChoices.add(Utils.createCommandChoice(name, name)); }
    public void removeChoice(String name) { commandChoices.remove(Utils.createCommandChoice(name, name)); }

    @Override
    public void stop(Context context) { commands.unregisterAll(); patcher.unpatchAll(); }
}