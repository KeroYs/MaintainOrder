package com.github.multidestroy.commands.mute;

import com.github.multidestroy.environment.MuteSystem;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnMute extends MaintainOrderCommand {

    private final MuteSystem muteSystem;

    public UnMute(MuteSystem muteSystem, Messages messages) {
        super(messages,"unmute", CommandPermissions.unmute, 1, 1);
        this.muteSystem = muteSystem;
    }

    @Override
    public void start(ProxiedPlayer executor, String[] args) throws WrongArgumentException {
        ProxiedPlayer recipient = getProxiedPlayer(args[0]);
        ServerInfo serverInfo = executor.getServer().getInfo();

        if (recipient != null && recipient.isConnected() && recipient.getServer().getInfo().getName().equals(serverInfo.getName())) {
            if (muteSystem.isPlayerMuted(serverInfo, recipient.getName())) {
                muteSystem.removePlayerMute(serverInfo, recipient.getName());

                executor.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.UNMUTE.SUCCESS")));
            } else {
                executor.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.UNMUTE.NOT_MUTED")));
            }
        } else {
            executor.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.PLAYER_OFFLINE")));
        }
    }

    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.UNMUTE.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.UNMUTE.HOVER_EVENT")
        );
    }

}