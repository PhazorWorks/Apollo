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
    private static List<SongCallBack> listeners = new ArrayList<>();

    public static void addListener(SongCallBack listener) {
        listeners.add(listener);
    }

    public static void notifyTrackLoaded(AudioTrack track) {
        for (SongCallBack listener : listeners) {
            listener.trackHasLoaded(track);
        }
    }

    public static void notifyPlaylistLoaded(AudioPlaylist playlist, int added, int amount) {
        for (SongCallBack listener : listeners) {
            listener.playlistLoaded(playlist, added, amount);
        }
    }

    public static void notifyNoMatches() {
        for (SongCallBack listener : listeners) {
            listener.noMatches();
        }
    }

    public static void notifyTrackLoadFailed(Exception e) {
        for (SongCallBack listener : listeners) {
            listener.trackLoadingFailed(e);
        }
    }

    public static void notifySpotifyUnsupported() {
        for (SongCallBack listener : listeners) {
            listener.spotifyUnsupported();
        }
    }

    public static void notifySpotifyAbort(Exception e) {
        for (SongCallBack listener : listeners) {
            listener.spotifyFailed(e);
        }
    }
}
