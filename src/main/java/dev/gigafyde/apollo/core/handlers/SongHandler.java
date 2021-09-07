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
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

public class SongHandler{
    private static List<SongCallBackListener> listeners = new ArrayList<>();
    public void addListener(SongCallBackListener listener) {
        listeners.add(listener);
    }
    public static void notifyTrackLoaded(AudioTrack track) {
        for (SongCallBackListener listener : listeners) {
            listener.trackHasLoaded(track);
        }
    }
    public static void notifySpotifyUnsupported() {
        for (SongCallBackListener listener : listeners) {
            listener.spotifyUnsupported();
        }
    }

    public static void loadHandler(TrackScheduler scheduler, String searchQuery, boolean search, boolean send) {
        scheduler.getManager().loadItem(search ? "ytsearch:" + searchQuery : searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (scheduler.addSong(track) && send) {
                    notifyTrackLoaded(track);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (search) {
                    if (playlist.getTracks().isEmpty()) {
                        noMatches();
                        return;
                    }
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    int added = scheduler.addSongs(playlist.getTracks());
//                            event.getChannel().sendMessage(String.format("**Added %s of %s from the playlist!**", added, playlist.getTracks().size())).mentionRepliedUser(false).queue();
                }
            }

            @Override
            public void noMatches() {
                if (search) {
//                            event.getChannel().sendMessage("**Failed to find anything with the term: **").mentionRepliedUser(true).queue();
                }
//                loadHandler(scheduler, event, true, user);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
//                        event.getChannel().sendMessage("**Failed to load track!**").mentionRepliedUser(false).queue();
                LoggerFactory.getLogger("SongHandler").warn("**Couldn't load track:** " + exception);
            }
        });
    }

}
//    private static void loadHandler(CommandEvent event, TrackScheduler scheduler, String searchQuery, boolean search, boolean send) {
//        scheduler.getManager().loadItem(search ? "ytsearch:" + searchQuery : searchQuery, new AudioLoadResultHandler() {
//            @Override
//            public void trackLoaded(AudioTrack track) {
//                if (scheduler.addSong(track) && send) {
//                    SongUtils.generateAndSendImage(event, track);
//                }
//            }
//
//            @Override
//            public void playlistLoaded(AudioPlaylist playlist) {
//                if (search) {
//                    if (playlist.getTracks().isEmpty()) {
//                        noMatches();
//                        return;
//                    }
//                    trackLoaded(playlist.getTracks().get(0));
//                } else {
//                    int added = scheduler.addSongs(playlist.getTracks());
//                    event.getMessage().reply(String.format("**Added %s of %s from the playlist!**", added, playlist.getTracks().size())).mentionRepliedUser(false).queue();
//                }
//            }
//
//            @Override
//            public void noMatches() {
//                if (search) {
//                    event.getMessage().reply("**Failed to find anything with the term: **" + event.getArgument()).mentionRepliedUser(true).queue();
//                }
////                loadHandler(scheduler, event, true, user);
//            }
//
//            @Override
//            public void loadFailed(FriendlyException exception) {
//                event.getMessage().reply("**Failed to load track!**").mentionRepliedUser(false).queue();
//                LoggerFactory.getLogger(Play.class).warn("**Couldn't load track:** " + exception);
//            }
//        });
//    }
