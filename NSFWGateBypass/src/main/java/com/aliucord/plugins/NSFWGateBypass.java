package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.discord.api.user.NsfwAllowance;

import com.aliucord.entities.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class NSFWGateBypass extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
        manifest.description = "Bypasses the NSFW age gate.";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json"; // change
        return manifest;
    }

    public static Map<String, List<String>> getClassesToPatch() {
        return new HashMap<String, List<String>>() {{
            put("com.discord.models.user.MeUser", Collections.singletonList("getNsfwAllowance"));
        }};
    }

    @Override
    public void start(Context context) {
        patcher.patch("com.discord.models.user.MeUser", "getNsfwAllowance", (_this, args, res) -> NsfwAllowance.ALLOWED);
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
