package com.github.multidestroy.commands.mute;

import com.github.multidestroy.environment.MuteSystem;
import com.github.multidestroy.environment.TimeArgument;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class Mute extends MaintainOrderCommand {

    private final MuteSystem muteSystem;

    public Mute(MuteSystem muteSystem, Messages messages) {
        super(messages,"mute", CommandPermissions.mute, 3);
        this.muteSystem = muteSystem;
    }

    @Override
    public void start(ProxiedPlayer executor, String[] args) throws WrongArgumentException {
        ProxiedPlayer recipient = getProxiedPlayer(args[0]);
        String reason = getReason(args, 2);
        TimeArgument timeArgument = new TimeArgument(args[1], false);

        if (recipient != null && recipient.isConnected() && isTheSameServer(executor, recipient)) {
            if (canMutePlayer(timeArgument, recipient)) {
                mutePlayer(timeArgument, recipient);

                //Send message to server
                SpecialTypeInfo info = new SpecialTypeInfo(recipient.getName(), executor.getName(), timeArgument.toString(), reason);
                sendGlobalMessage(executor.getServer().getInfo(), getSpecialMessage(SpecialType.COMMAND_MUTE_GLOBAL, info));
            } else {
                executor.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.MUTE.ALREADY_MUTED")));
            }
        } else {
            executor.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.PLAYER_OFFLINE")));
        }
    }

    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.MUTE.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.MUTE.HOVER_EVENT")
        );
    }

    private boolean isTheSameServer(ProxiedPlayer player1, ProxiedPlayer player2) {
        return player1.getServer().getInfo().getName().equalsIgnoreCase(player2.getServer().getInfo().getName());
    }

    private boolean canMutePlayer(TimeArgument timeArgument, ProxiedPlayer player) {
        return muteSystem.getPlayerMuteExpiration(player.getServer().getInfo(), player.getName()).isBefore(timeArgument.getFutureTime());
    }

    private void mutePlayer(TimeArgument timeArgument, ProxiedPlayer player) {
        muteSystem.givePlayerMute(player.getServer().getInfo(), player.getName(), timeArgument.getFutureTime());
    }

}