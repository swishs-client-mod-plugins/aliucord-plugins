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
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
        manifest.description = "Bypasses the NSFW age gate.";
        manifest.version = "1.0.1";
        manifest.updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json";
        return manifest;
    }
    private static final String className = "com.discord.models.user.MeUser";
    public static Map<String, List<String>> getClassesToPatch() {
        return new HashMap<String, List<String>>() {{
            put(className, Arrays.asList("getNsfwAllowance", "getHasBirthday"));
        }};
    }

    @Override
    public void start(Context context) {
        patcher.patch(className, "getNsfwAllowance", (_this, args, res) -> NsfwAllowance.ALLOWED);
        patcher.prePatch(className, "getHasBirthday", (_this, args) -> new PrePatchRes(args, true));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
