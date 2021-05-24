/*
 * Copyright (c) 2021 Paige
 * Licensed under the GNU General Public License v3.0
 */

package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.patcher.PrePatchRes;
import com.discord.api.user.NsfwAllowance;

import com.aliucord.entities.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class NSFWGateBypass extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        return new Manifest() {{
            authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
            description = "Bypasses the NSFW age gate.";
            version = "1.0.2";
            updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json";
        }};
    }
    private static final String currentUserClass = "com.discord.models.user.MeUser";
    public static Map<String, List<String>> getClassesToPatch() {
        return new HashMap<String, List<String>>() {{
            put(currentUserClass, Arrays.asList("getNsfwAllowance", "getHasBirthday"));
        }};
    }

    @Override
    public void start(Context context) {
        patcher.patch(currentUserClass, "getNsfwAllowance", (_this, args, res) -> NsfwAllowance.ALLOWED);
        patcher.prePatch(currentUserClass, "getHasBirthday", (_this, args) -> new PrePatchRes(true));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
