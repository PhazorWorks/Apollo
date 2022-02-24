package dev.gigafyde.apollo.core.handlers;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import dev.gigafyde.apollo.core.TrackScheduler;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SongHandler {
    private static final List<AudioTrack> playlistTracks = new ArrayList<AudioTrack>();
    private static int amountOfTracks = 0;

    public static void loadPlaylistHandler(Object key, String name, int amount, TrackScheduler scheduler, String searchQuery, boolean search, boolean send, String author) {
        if (amountOfTracks == 0) amountOfTracks = amount;
        scheduler.getManager().loadItemOrdered(key, search ? "ytsearch:" + searchQuery : searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                playlistTracks.add(track);
                scheduler.addTrack(track, author);
                if (playlistTracks.size() == amountOfTracks) {
                    BasicAudioPlaylist basicPlaylist = new BasicAudioPlaylist(name, playlistTracks, null, false);
                    SongCallBackListener.notifyPlaylistLoaded(basicPlaylist, basicPlaylist.getTracks().size(), amount);
                    amountOfTracks = 0;
                    playlistTracks.clear();
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
                    int added = scheduler.addTracks(author, playlist.getTracks());
                    int amount = playlist.getTracks().size();
                    SongCallBackListener.notifyPlaylistLoaded(playlist, added, amount);
                }
            }

            @Override
            public void noMatches() {
                if (search) {
                    amountOfTracks -= 1;
                    SongCallBackListener.notifyNoMatches();
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                amountOfTracks -= 1;
                SongCallBackListener.notifyTrackLoadFailed(exception);
                LoggerFactory.getLogger("SongHandler").warn("**Couldn't load track:** " + exception);
            }
        });
    }

    public static void loadHandler(TrackScheduler scheduler, String searchQuery, boolean search, boolean send, String author) {
        scheduler.getManager().loadItem(search ? "ytsearch:" + searchQuery : searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (scheduler.addTrack(track, author) && send) {
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
                    int added = scheduler.addTracks(author, playlist.getTracks());
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
