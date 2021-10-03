package dev.gigafyde.apollo.core.handlers;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import org.slf4j.LoggerFactory;

public class SongHandler {
    public static void loadPlaylistHandler(Object key, TrackScheduler scheduler, String searchQuery, boolean search, boolean send) {
        scheduler.getManager().loadItemOrdered(key, search ? "ytsearch:" + searchQuery : searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (scheduler.addSong(track) && send) {
                    SongCallBackListener.notifyTrackLoaded(track);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (search) {
                    if (playlist.getTracks().isEmpty()) {
                        noMatches();
                    }
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    int added = scheduler.addSongs(playlist.getTracks());
                    int amount = playlist.getTracks().size();
                    SongCallBackListener.notifyPlaylistLoaded(playlist, added, amount);
                }
            }

            @Override
            public void noMatches() {
                if (search) {
                    SongCallBackListener.notifyNoMatches();
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                SongCallBackListener.notifyTrackLoadFailed(exception);
                LoggerFactory.getLogger("SongHandler").warn("**Couldn't load track:** " + exception);
            }
        });
    }

    public static void loadHandler(TrackScheduler scheduler, String searchQuery, boolean search, boolean send) {
        scheduler.getManager().loadItem(search ? "ytsearch:" + searchQuery : searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (scheduler.addSong(track) && send) {
                    SongCallBackListener.notifyTrackLoaded(track);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (search) {
                    if (playlist.getTracks().isEmpty()) {
                        noMatches();
                    }
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    int added = scheduler.addSongs(playlist.getTracks());
                    int amount = playlist.getTracks().size();
                    SongCallBackListener.notifyPlaylistLoaded(playlist, added, amount);
                }
            }

            @Override
            public void noMatches() {
                if (search) {
                    SongCallBackListener.notifyNoMatches();
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                SongCallBackListener.notifyTrackLoadFailed(exception);
                LoggerFactory.getLogger("SongHandler").warn("**Couldn't load track:** " + exception);
            }
        });
    }
}
