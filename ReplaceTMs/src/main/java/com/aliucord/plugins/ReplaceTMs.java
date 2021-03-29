package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PrePatchRes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ReplaceTMs extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        return new Manifest() {{
            authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
            description = "Replaces the \":tm:\" emoji in all messages with the actual ™ character.";
            version = "1.0.0";
            updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json";
        }};
    }

    public static Map<String, List<String>> getClassesToPatch() {
        return new HashMap<String, List<String>>() {{
            put("com.discord.widgets.chat.MessageManager", Collections.singletonList("sendMessage"));
            put("com.discord.models.domain.ModelMessage", Collections.singletonList("getContent"));
        }};
    }

    @Override
    public void start(Context context) {
        patcher.prePatch("com.discord.widgets.chat.MessageManager", "sendMessage", (_this, args) -> {
            args.set(0, args.get(0).toString().replaceAll("™️", "™"));
            return new PrePatchRes(args);
        });

        patcher.patch("com.discord.models.domain.ModelMessage", "getContent", (_this, args, res) -> {
            return res.toString().replaceAll("™️", "™");
        });
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
