package dev.gigafyde.apollo.core.handlers;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface SongCallBack {
    void trackHasLoaded(AudioTrack track);

    void playlistLoaded(AudioPlaylist playlist, int added, int amount);

    void noMatches();

    void trackLoadingFailed(Exception e);

    void spotifyUnsupported();

    void spotifyFailed(Exception e);
}
