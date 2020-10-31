package com.github.multidestroy.commands.kick;

import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Kick extends MaintainOrderCommand {

    public Kick(Messages messages) {
        super(messages, "kick", CommandPermissions.kick, 2);
    }

    protected Kick(String commandName, String commandPermission, Messages messages) {
        super(messages, commandName, commandPermission, 2);
    }

    @Override
    public final void start(ProxiedPlayer executor, String[] args) throws WrongArgumentException {
        ProxiedPlayer recipient = getProxiedPlayer(args[0]);
        String reason = getReason(args, 1);

        if (recipient != null && canKickPlayer(executor, recipient)) {
            SpecialTypeInfo specialTypeInfo = new SpecialTypeInfo(recipient.getName(), executor.getName(), "", reason);

            recipient.disconnect(TextComponent.fromLegacyText(
                    SpecialType.COMMAND_KICK_RECEIVER.getString(messages, specialTypeInfo)));

            sendGlobalMessage(executor.getServer().getInfo(),
                    TextComponent.fromLegacyText(SpecialType.COMMAND_KICK_GLOBAL.getString(messages, specialTypeInfo)));
        } else {
            executor.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.INCORRECT_USAGE.PLAYER_OFFLINE")));
        }
    }

    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.KICK.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.KICK.HOVER_EVENT")
        );
    }

    protected boolean canKickPlayer(ProxiedPlayer player1, ProxiedPlayer player2) {
        if (player1.isConnected() && player2.isConnected())
            return player1.getServer().getInfo().getName().equals(player2.getServer().getInfo().getName());

        return false;
    }

}