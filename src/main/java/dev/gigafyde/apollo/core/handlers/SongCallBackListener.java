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
        try {
            for (var listener : listeners) {
                listener.trackHasLoaded(track);
            }
        } catch (Exception ignored) {
            //Will fail if it can't find any listeners
        }
    }

    public static void notifyPlaylistLoaded(AudioPlaylist playlist, int added, int amount) {
        try {
            for (var listener : listeners) {
                listener.playlistLoaded(playlist, added, amount);
            }
        } catch (Exception ignored) {
            //Will fail if it can't find any listeners
        }
    }

    public static void notifyNoMatches() {
        try {
            for (var listener : listeners) {
                listener.noMatches();
            }
        } catch (Exception ignored) {
            //Will fail if it can't find any listeners
        }
        listeners.get(0).noMatches();
    }

    public static void notifyTrackLoadFailed(Exception e) {
        try {
            for (var listener : listeners) {
                listener.trackLoadingFailed(e);
            }
        } catch (Exception ignored) {
            //Will fail if it can't find any listeners
        }
    }

    public static void notifySpotifyUnsupported() {
        try {
            for (var listener : listeners) {
                listener.spotifyUnsupported();
            }
        } catch (Exception ignored) {
            //Will fail if it can't find any listeners
        }
    }

    public static void notifySpotifyAbort(Exception e) {
        try {
            for (var listener : listeners) {
                listener.spotifyFailed(e);
            }
        } catch (Exception ignored) {
            //Will fail if it can't find any listeners
        }
    }
}
