package dev.gigafyde.apollo.core.handlers;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.List;

public class SongCallBackListener {
    private static final List<SongCallBack> listeners = new ArrayList<>();

    public static void addListener(SongCallBack listener) {
        listeners.add(listener);
    }

    public static void removeListener(SongCallBack listener) {
        listeners.remove(listener);
    }

    public static void notifyTrackLoaded(AudioTrack track) {
        listeners.get(0).trackHasLoaded(track);
    }

    public static void notifyPlaylistLoaded(AudioPlaylist playlist, int added, int amount) {
        listeners.get(0).playlistLoaded(playlist, added, amount);
    }

    public static void notifyNoMatches() {
        listeners.get(0).noMatches();
    }

    public static void notifyTrackLoadFailed(Exception e) {
        listeners.get(0).trackLoadingFailed(e);
    }

    public static void notifySpotifyUnsupported() {
        listeners.get(0).spotifyUnsupported();
    }

    public static void notifySpotifyAbort(Exception e) {

        listeners.get(0).spotifyFailed(e);

    }
}
