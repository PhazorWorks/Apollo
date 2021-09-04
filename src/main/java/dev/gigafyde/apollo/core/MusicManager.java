package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public final class MusicManager {
    private final Client client;
    private final Map<Long, TrackScheduler> schedulers = new ConcurrentHashMap<>();
    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    public MusicManager(Client client) {
        this.client = client;
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public TrackScheduler getScheduler(Guild guild) {
        return schedulers.get(guild.getIdLong());
    }

    public void disconnect(Guild guild) {
        schedulers.remove(guild.getIdLong());
    }

    public void moveVoiceChannel(VoiceChannel voiceChannel) {
        Guild guild = voiceChannel.getGuild();
        JdaLink link = client.getLavalink().getLink(guild);
        AudioManager audioManager = guild.getAudioManager();
        if (!audioManager.isConnected()) {
            addScheduler(voiceChannel, true);
        } else {
            link.connect(voiceChannel);
        }
    }

    public TrackScheduler addScheduler(VoiceChannel voiceChannel, boolean start) {
        Guild guild = voiceChannel.getGuild();
        JdaLink link = client.getLavalink().getLink(guild);
        link.connect(voiceChannel);
        IPlayer player = link.getPlayer();
        player.setVolume(100);
        TrackScheduler scheduler = new TrackScheduler(player, playerManager, guild, start);
        player.addListener(scheduler);
        schedulers.put(guild.getIdLong(), scheduler);
        return scheduler;
    }
}
