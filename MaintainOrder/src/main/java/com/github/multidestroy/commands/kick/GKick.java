package com.github.multidestroy.commands.kick;

import com.github.multidestroy.Config;
import com.github.multidestroy.Messages;
import com.github.multidestroy.SoundChannel;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GKick extends Command {

    private final Messages messages;
    private final Config config;
    private final Config notificationsConfig;

    public GKick(Messages messages, Config config, Config notificationsConfig) {
        super("gkick", CommandPermissions.gkick);
        this.messages = messages;
        this.config = config;
        this.notificationsConfig = notificationsConfig;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent correctUsage =
                Utils.createHoverEvent_OneDesc(notificationsConfig, "commands.gkick.hover_event", "commands.gkick.correct_usage");
        if (args.length < 2) {
            sender.sendMessage(correctUsage);
        } else {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player == null)
                sender.sendMessage(TextComponent.fromLegacyText(notificationsConfig.get().getString("bad_usage.player_offline")));
            else {
                String reason = Utils.mergeArray(args, 1);
                if(!Utils.isUnderLimit(sender, notificationsConfig, args[0], reason))
                    return;

                player.disconnect(messages.getKickReceiver(sender.getName(), reason));
                ProxyServer.getInstance().broadcast(messages.getKickGlobal(player.getName(), sender.getName(), reason));
                SoundChannel.sendNetworkSound(config.get().getString("sound.gkick"));
            }
        }
    }
}