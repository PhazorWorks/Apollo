package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.github.topisenpai.lavasrc.applemusic.AppleMusicSourceManager;
import com.github.topisenpai.lavasrc.spotify.SpotifySourceManager;
import com.github.topisenpai.lavasrc.deezer.DeezerAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import java.util.HashMap;
import java.util.Map;

import dev.gigafyde.apollo.Main;
import net.dv8tion.jda.api.entities.Guild;

public final class MusicManager {
    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private static MusicManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;

    public MusicManager() {

        this.musicManagers = new HashMap<>();
        if (Main.SPOTIFY_CLIENT_ID != null) playerManager.registerSourceManager(new SpotifySourceManager(null, Main.SPOTIFY_CLIENT_ID, Main.SPOTIFY_CLIENT_SECRET, "US", playerManager));
        if (Main.DEEZER_KEY != null) playerManager.registerSourceManager(new DeezerAudioSourceManager(Main.DEEZER_KEY));
        playerManager.registerSourceManager(new AppleMusicSourceManager(null, null, "US", playerManager));

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

    }

    public static synchronized MusicManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MusicManager();
        }

        return INSTANCE;
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

}
