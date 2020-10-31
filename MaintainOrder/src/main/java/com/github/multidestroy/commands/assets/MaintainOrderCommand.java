package com.github.multidestroy.commands.assets;

import com.github.multidestroy.MainPluginClass;
import com.github.multidestroy.environment.TimeArgument;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import com.github.multidestroy.i18n.SpecialType;
import com.github.multidestroy.i18n.SpecialTypeInfo;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public abstract class MaintainOrderCommand extends Command {

    private final int minArgNumber;
    private final int maxArgNumber;
    @Getter
    private final TextComponent correctUsage;
    protected final Messages messages;

    public MaintainOrderCommand(Messages messages, String name, String permission, int minArgNumber) {
        super(name, permission);
        this.messages = messages;
        this.minArgNumber = minArgNumber;
        this.maxArgNumber = Integer.MAX_VALUE;
        this.correctUsage = createCorrectUsage();
    }

    public MaintainOrderCommand(Messages messages, String name, String permission, int minArgNumber, int maxArgNumber) {
        super(name, permission);
        this.messages = messages;
        this.minArgNumber = minArgNumber;
        this.maxArgNumber = maxArgNumber;
        this.correctUsage = createCorrectUsage();
    }

    @Override
    public final void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length >= minArgNumber && args.length <= maxArgNumber) {

                //Perform command
                try {
                    start((ProxiedPlayer) sender, args);
                } catch (WrongArgumentException ex) {
                    sender.sendMessage(TextComponent.fromLegacyText(ex.getMessage()));
                }
            } else {
                sender.sendMessage(correctUsage);
            }
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Console can not execute this command!"));
        }
    }

    public abstract void start(ProxiedPlayer executor, String[] args) throws WrongArgumentException;

    protected abstract TextComponent createCorrectUsage();

    protected BaseComponent[] getSpecialMessage(SpecialType specialType, SpecialTypeInfo specialTypeInfo) {
        return TextComponent.fromLegacyText(specialType.getString(messages, specialTypeInfo));
    }

    protected final String getReason(String[] reason, int beg) throws WrongArgumentException {
        StringBuilder sb = new StringBuilder();
        for (int i = beg; i < reason.length && sb.length() <= 300; i++) {
            sb.append(reason[i]);
            if (i + 1 < reason.length)
                sb.append(' ');
        }

        if (sb.length() >= 300) {
            throw new WrongArgumentException(ChatColor.RED + "Maximum reason length: 300 characters");
        }

        return sb.toString();
    }

    protected final String getPlayer(String nick) throws WrongArgumentException {
        if (nick.length() >= 17) {
            throw new WrongArgumentException(ChatColor.RED + "Maximum nickname length: 17 characters");
        }

        return nick;
    }

    protected ProxiedPlayer getProxiedPlayer(String wantedPlayer) throws WrongArgumentException {
        return ProxyServer.getInstance().getPlayer(getPlayer(wantedPlayer));
    }

    protected void sendGlobalMessage(ServerInfo server, BaseComponent[] message) {
        server.getPlayers().forEach(player -> player.sendMessage(message));
    }
}
