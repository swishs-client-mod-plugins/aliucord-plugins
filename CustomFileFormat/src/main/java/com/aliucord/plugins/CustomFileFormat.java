/*
 * Copyright (c) 2021 Paige
 * Licensed under the GNU General Public License v3.0
 */

package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.entities.Plugin;
import com.aliucord.plugins.customfileformat.PluginSettings;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@SuppressWarnings("unused")
public class CustomFileFormat extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        return new Manifest() {{
            authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
            description = "Lets you customize the file format of uploaded/downloaded files.";
            version = "1.0.1";
            updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json";
        }};
    }

    public static Map<String, List<String>> getClassesToPatch() {
        return new HashMap<String, List<String>>() {{
            put("com.discord.utilities.attachments.AttachmentUtilsKt", Collections.singletonList("getSanitizedFileName"));
            put("com.discord.utilities.io.NetworkUtils", Collections.singletonList("downloadFile"));
        }};
    }

    @Override
    public void start(Context context) {
        patcher.patch("com.discord.utilities.attachments.AttachmentUtilsKt", "getSanitizedFileName", (_this, args, ret) -> {
            return uploadFormat(ret.toString());
        });

        patcher.prePatch("com.discord.utilities.io.NetworkUtils", "downloadFile", (_this, args) -> {
            args.set(2, downloadFormat((String) args.get(2)));
            return null;
        });
    }

    public String uploadFormat(String file) {
        return sets.getString("uploadFormat", "{original}")
            .replace("{original}", fileName(file))
            .replace("{random}", randomFileName())
            .replace("{timestamp}", currentTime()) + fileExtension(file);
    }

    public String downloadFormat(String file) {
        return sets.getString("downloadFormat", "{original}")
            .replace("{original}", fileName(file))
            .replace("{random}", randomFileName())
            .replace("{timestamp}", currentTime()) + fileExtension(file);
    }

    public String fileName(String file) {
        Matcher matcher = Pattern.compile("(.*)(\\.[0-9a-z]+$)", Pattern.CASE_INSENSITIVE).matcher(file);
        if (matcher.find()) return matcher.group(1);
        return "";
    }

    public String fileExtension(String file) {
        Matcher matcher = Pattern.compile("\\.[0-9a-z]+$", Pattern.CASE_INSENSITIVE).matcher(file);
        if (matcher.find()) return matcher.group(0);
        return "";
    }

    public String randomFileName() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public String currentTime() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
    public CustomFileFormat() { settings = new Settings(PluginSettings.class); }
}