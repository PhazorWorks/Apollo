package dev.gigafyde.apollo.core.handlers;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.TrackScheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpotifyHandler {
    private static final Logger log = LoggerFactory.getLogger("SpotifyHandler");
    private static TrackScheduler scheduler;
    private static List<SongCallBackListener> listeners = new ArrayList<>();
    public void addListener(SongCallBackListener listener) {
        listeners.add(listener);
    }

    public static void notifySpotifyAbort(Exception e) {
        for (SongCallBackListener listener : listeners) {
            listener.spotifyFailed(e);
        }
    }

    public static void handleSpotify(TrackScheduler trackScheduler, String input) {
        String[] url = input.split("/");
        String[] object = url[4].split("\\?si");
        scheduler = trackScheduler;
        if (Main.SPOTIFY_WEB_SERVER.isEmpty()) return;
        if (input.contains("track")) {
            handleSpotifyTrack(object[0]);
        } else if (input.contains("playlist")) {
            handleSpotifyPlaylist(object[0]);
        } else {
            SongHandler.notifySpotifyUnsupported();
        }
    }

    public static void handleSpotifyTrack(String trackId) {
        try {
            Response response = Main.httpClient.newCall(
                    new Request.Builder()
                            .url(Main.SPOTIFY_WEB_SERVER + "track" + "?id=" + trackId)
                            .build()).execute();
            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response.body()).string());
            JSONObject jsonObject = jsonResponse.getJSONObject("track");
            String artist = jsonObject.get("artist").toString();
            String title = jsonObject.get("name").toString();
            SongHandler.loadHandler(scheduler, artist + " " + title, true, true);
        } catch (Exception e) {
            notifySpotifyAbort(e);
            log.error("Spotify Lookup failed! Aborting");


//            event.getMessage().reply("Spotify Lookup failed! Aborting").mentionRepliedUser(true).queue();
        }
    }

    private static void handleSpotifyPlaylist(String playlistId) {
        try {
            Response response = Main.httpClient.newCall(
                    new Request.Builder()
                            .url(Main.SPOTIFY_WEB_SERVER + "playlist" + "?id=" + playlistId)
                            .build()).execute();
            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response.body()).string());
            JSONObject jsonObject = jsonResponse.getJSONObject("playlist");
            JSONArray tracks = jsonObject.getJSONArray("tracks");
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i).getJSONObject("track");
                String artist = track.get("artist").toString();
                String title = track.get("name").toString();
                SongHandler.loadHandler(scheduler, artist + " " + title, true, false);
            }
//            event.getMessage().reply(String.format("**Added %s tracks from the playlist!**", tracks.length())).mentionRepliedUser(false).queue();
        } catch (Exception e) {
//            event.getMessage().reply("Spotify Lookup failed! Aborting").mentionRepliedUser(true).queue();
        }
    }

//    private void handleSpotifyTrack(SlashEvent event, TrackScheduler scheduler, String[] argument) {
//        String object = argument[4];
//        String[] objectId = object.split("\\?si");
//        String WebServerEndpoint = System.getenv("SPOTIFY_WEB_SERVER");
//        try {
//            Response response = Main.httpClient.newCall(
//                    new Request.Builder()
//                            .url(WebServerEndpoint + "track" + "?id=" + objectId[0])
//                            .build()).execute();
//            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response.body()).string());
//            JSONObject jsonObject = jsonResponse.getJSONObject("track");
//            String artist = jsonObject.get("artist").toString();
//            String title = jsonObject.get("name").toString();
//            SongHandler.loadHandler(artist + " " + title, true, true);
//        } catch (Exception e) {
//            event.reply("Spotify Lookup failed! Aborting").mentionRepliedUser(true).queue();
//        }
//    }

//    private void handleSpotify(CommandEvent event) {
//        MusicManager musicManager = event.getClient().getMusicManager();
//        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
//        String[] argument = event.getArgument().split("/");
//        String route = argument[3];
//        EnumSet<Permission> channelPermissions = event.getSelfMember().getPermissions((GuildChannel) event.getChannel());
//        if (channelPermissions.contains(Permission.MESSAGE_MANAGE)) event.getMessage().suppressEmbeds(true).queue();
//        if (route.contains("track")) {
//            handleSpotifyTrack(event, scheduler, argument);
//        } else if (route.contains("playlist")) {
//            handleSpotifyPlaylist(event, scheduler, argument);
//        } else {
//            event.getMessage().reply("Invalid/Unsupported Spotify URL!").mentionRepliedUser(true).queue();
//        }
//    }
//
//    private void handleSpotify(SlashEvent event) {
//        MusicManager musicManager = event.getClient().getMusicManager();
//        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
//        String[] argument = event.getSlashCommandEvent().getOption("args").getAsString().split("/");
//        String route = argument[3];
//        if (route.contains("track")) {
//            handleSpotifyTrack(event, scheduler, argument);
////        } else if (route.contains("playlist")) {
////            handleSpotifyPlaylist(event, scheduler, argument);
//        } else {
//            event.reply("Invalid/Unsupported Spotify URL!").mentionRepliedUser(true).queue();
//        }
//    }

}
