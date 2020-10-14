package com.github.multidestroy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class SoundPlayer extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        checkIfBungee();
        if (!getServer().getPluginManager().isPluginEnabled(this))
            return;

        getServer().getMessenger().registerIncomingPluginChannel(this, "maintain_order:sound", this);
        getLogger().info( "<MaintainOrder-SoundPlayer> driver enabled successfully.");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes)
    {
        if (!channel.equalsIgnoreCase("maintain_order:sound"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput( bytes );
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("SoundChannel")) {
            String soundAsString = in.readUTF();
            try {
                System.out.println(soundAsString.length());
                if(soundAsString.length() == 0)
                    return;

                Sound sound = Sound.valueOf(soundAsString);

                player.getWorld().playSound(player.getLocation(), sound, 100, 0);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkIfBungee() {
        //check if the server is Spigot/Paper (because of the spigot.yml file)
        if (!getServer().getVersion().contains( "Spigot" ) && !getServer().getVersion().contains( "Paper" )) {
            getLogger().severe( "You probably run CraftBukkit... Please update atleast to spigot for this to work..." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
            return;
        }
        if (getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("settings.bungeecord")) {
            getLogger().severe( "This server is not BungeeCord." );
            getLogger().severe( "If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin( this );
        }
    }

}
