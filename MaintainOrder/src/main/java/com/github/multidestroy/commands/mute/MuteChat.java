package com.github.multidestroy.commands.mute;

import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import com.github.multidestroy.environment.MuteSystem;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MuteChat extends MaintainOrderCommand {

    private final MuteSystem muteSystem;

    public MuteChat(MuteSystem muteSystem, Messages messages) {
        super(messages, "mutechat", CommandPermissions.mutechat, 0, 1);
        this.muteSystem = muteSystem;
    }

    @Override
    public void start(ProxiedPlayer executor, String[] args) throws WrongArgumentException {
        ServerInfo serverInfo = executor.getServer().getInfo();
        boolean currStatus = muteSystem.getChatStatus(serverInfo);
        boolean newStatus = getNewStatus(serverInfo, args);

        if (currStatus != newStatus) {
            muteSystem.setChatStatus(serverInfo, newStatus);

            //Send message to server
            SpecialTypeInfo info = new SpecialTypeInfo("", executor.getName(), "", "");
            sendGlobalMessage(serverInfo,
                    getSpecialMessage(
                            newStatus ? SpecialType.COMMAND_MUTECHAT_ON : SpecialType.COMMAND_MUTECHAT_OFF,
                            info));
        } else {
            executor.sendMessage(TextComponent.fromLegacyText(messages.getString("NORMAL.COMMAND.MUTECHAT.SAME_STATUS")));
        }
    }

    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.MUTECHAT.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.MUTECHAT.HOVER_EVENT")
        );
    }

    private boolean getNewStatus(ServerInfo serverInfo, String[] args) throws WrongArgumentException {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on"))
                return true;
            if (args[0].equalsIgnoreCase("off"))
                return false;

            throw new WrongArgumentException(ChatColor.RED + "Chat statuses: (ON, OFF)");
        }
        return !muteSystem.getChatStatus(serverInfo);
    }

}