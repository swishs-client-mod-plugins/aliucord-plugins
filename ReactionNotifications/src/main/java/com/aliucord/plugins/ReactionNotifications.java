/*
 * Copyright (c) 2021 Paige
 * Licensed under the GNU General Public License v3.0
 */

package com.aliucord.plugins;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aliucord.Utils;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.entities.NotificationData;
import com.aliucord.entities.Plugin;

import com.discord.api.channel.Channel;
import com.discord.api.message.reaction.MessageReactionUpdate;
import com.discord.models.domain.ModelMessage;
import com.discord.models.user.CoreUser;
import com.discord.stores.StoreStream;
import com.discord.utilities.channel.ChannelSelector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;

@SuppressWarnings("unused")
public class ReactionNotifications extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        return new Manifest() {{
            authors = new Manifest.Author[]{new Manifest.Author("Swishilicous", 474322346937810955L)};
            description = "Sends you a notification when someone reacts to your messages.";
            version = "1.0.0";
            updateUrl = "https://raw.githubusercontent.com/swishs-client-mod-plugins/aliucord-plugins/builds/updater.json";
        }};
    }

    public static String storeMessageReactionsClass = "com.discord.stores.StoreMessageReactions";
    public static Map<String, List<String>> getClassesToPatch() {
        return new HashMap<String, List<String>>() {{
            put(storeMessageReactionsClass, Arrays.asList("handleReactionAdd", "handleReactionRemove"));
        }};
    }

    @Override
    public void start(Context context) {
        patcher.patch(storeMessageReactionsClass, "handleReactionAdd", (_this, args, res) -> {
            handleReactionData(args, false); return res;
        });

        patcher.patch(storeMessageReactionsClass, "handleReactionRemove", (_this, args, res) -> {
            handleReactionData(args, true); return res;
        });
    }

    private void handleReactionData(List<Object> args, boolean isRemoved) {
        MessageReactionUpdate reaction = (MessageReactionUpdate) args.get(0);
        ModelMessage message = StoreStream.getMessages().getMessage(reaction.a(), reaction.c());
        if (StoreStream.getUsers().getMe().getId() == message.getAuthor().f()) {
            CoreUser user = (CoreUser) StoreStream.getUsers().getUsers().get(reaction.d());
            Channel channel = StoreStream.getChannels().getChannel(message.getChannelId());
            NotificationData notificationData = new NotificationData() {{
                title = "Reaction Added";
                body = Utils.renderMD(
                    "**" + user.getUsername() + "#" + user.getDiscriminator()
                     + (isRemoved ? "** removed reaction " : "** reacted with ") + reaction.b().c()
                     + " in **" + (channel.l() != null ? "#" + channel.l() : "DMs") + "**."
                );
                iconUrl = "https://cdn.discordapp.com/avatars/" + user.getId()
                     + "/" + user.getAvatar() + ".png?size=128";
                autoDismissPeriodSecs = 5;
                onClick = v -> {
                    ChannelSelector.getInstance().findAndSet(v.getContext(), reaction.a());
                    return Unit.a;
                };
            }};
            NotificationsAPI.display(notificationData);
        }
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
