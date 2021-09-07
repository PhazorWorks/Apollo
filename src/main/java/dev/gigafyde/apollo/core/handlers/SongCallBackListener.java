package dev.gigafyde.apollo.core.handlers;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface SongCallBackListener {
    void trackHasLoaded(AudioTrack track);
    void trackLoadingFailed(Exception e);
    void spotifyUnsupported();
    void spotifyFailed(Exception e);
}
