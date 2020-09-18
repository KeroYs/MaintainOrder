package com.github.multidestroy.commands.assets;

import com.github.multidestroy.commands.Gungan;
import com.github.multidestroy.commands.HelpMO;
import com.github.multidestroy.commands.Info;
import com.github.multidestroy.commands.ReloadMO;
import com.github.multidestroy.commands.bans.Ban;
import com.github.multidestroy.commands.bans.GBan;
import com.github.multidestroy.commands.bans.GunBan;
import com.github.multidestroy.commands.bans.UnBan;
import com.github.multidestroy.commands.kick.GKick;
import com.github.multidestroy.commands.kick.Kick;
import com.github.multidestroy.commands.mute.Mute;
import com.github.multidestroy.commands.mute.MuteChat;
import com.github.multidestroy.commands.mute.UnMute;

public class CommandsStructure {
    public final Ban ban;
    public final GBan gBan;
    public final GunBan gunBan;
    public final UnBan unBan;
    public final GKick gKick;
    public final Kick kick;
    public final Mute mute;
    public final MuteChat muteChat;
    public final UnMute unMute;
    public final Gungan gungan;
    public final HelpMO helpMO;
    public final Info info;
    public final ReloadMO reloadMO;

    public CommandsStructure(Ban ban, GBan gBan, GunBan gunBan, UnBan unBan, GKick gKick, Kick kick, Mute mute, MuteChat muteChat, UnMute unMute, Gungan gungan, HelpMO helpMO, Info info, ReloadMO reloadMO) {
        this.ban = ban;
        this.gBan = gBan;
        this.gunBan = gunBan;
        this.unBan = unBan;
        this.gKick = gKick;
        this.kick = kick;
        this.mute = mute;
        this.muteChat = muteChat;
        this.unMute = unMute;
        this.gungan = gungan;
        this.helpMO = helpMO;
        this.info = info;
        this.reloadMO = reloadMO;
    }
}
