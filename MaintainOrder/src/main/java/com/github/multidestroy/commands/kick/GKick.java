package com.github.multidestroy.commands.kick;

import com.github.multidestroy.Config;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.SoundChannel;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.Utils;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GKick extends Command {

    private final Messages messages;
    private final Config config;

    public GKick(Messages messages, Config config) {
        super("gkick", CommandPermissions.gkick);
        this.messages = messages;
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent correctUsage =
                Utils.createHoverEvent(
                        messages.getString("NORMAL.COMMAND.GKICK.CORRECT_USAGE"),
                        messages.getString("NORMAL.COMMAND.GKICK.HOVER_EVENT")
                );
        if (args.length < 2) {
            sender.sendMessage(correctUsage);
        } else {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player == null)
                sender.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.PLAYER_OFFLINE")));
            else {
                String reason = Utils.mergeArray(args, 1);
                if(!Utils.isUnderLimit(sender, messages, args[0], reason))
                    return;

                SpecialTypeInfo specialTypeInfo = new SpecialTypeInfo();
                {
                    specialTypeInfo.setGiver(sender.getName());
                    specialTypeInfo.setReason(reason);
                    specialTypeInfo.setReceiver(player.getName());
                }

                player.disconnect(TextComponent.fromLegacyText(messages.getSpecialMessage(
                        SpecialType.COMMAND_KICK_RECEIVER,
                        specialTypeInfo
                )));
                ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(messages.getSpecialMessage(
                        SpecialType.COMMAND_KICK_GLOBAL,
                        specialTypeInfo
                )));
                SoundChannel.sendNetworkSound(config.get().getString("sound.gkick"));
            }
        }
    }
}