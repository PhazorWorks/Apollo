package dev.gigafyde.apollo.core.handlers;

import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.TrackScheduler;
import java.util.Objects;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class SpotifyHandler {
    private static final String WebServerEndpoint = System.getenv("SPOTIFY_WEB_SERVER");
    private static TrackScheduler scheduler;

    public static void handleSpotify(TrackScheduler trackScheduler, String url) {
        scheduler = trackScheduler;
        if (WebServerEndpoint.isEmpty()) return;
        String[] objectId = url.split("\\?si");
        if (url.contains("track")) {
            handleSpotifyTrack(objectId[0]);
        } else if (url.contains("playlist")) {
            handleSpotifyPlaylist(objectId[0]);
        }
        //     else       event.getMessage().reply("Invalid/Unsupported Spotify URL!").mentionRepliedUser(true).queue();

    }

    public static void handleSpotifyTrack(String trackId) {
        try {
            Response response = Main.httpClient.newCall(
                    new Request.Builder()
                            .url(WebServerEndpoint + "track" + "?id=" + trackId)
                            .build()).execute();
            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response.body()).string());
            JSONObject jsonObject = jsonResponse.getJSONObject("track");
            String artist = jsonObject.get("artist").toString();
            String title = jsonObject.get("name").toString();
            SongHandler.loadHandler(scheduler, artist + " " + title, true, true);
        } catch (Exception e) {
//            event.getMessage().reply("Spotify Lookup failed! Aborting").mentionRepliedUser(true).queue();
        }
    }

    private static void handleSpotifyPlaylist(String playlistId) {
        try {
            Response response = Main.httpClient.newCall(
                    new Request.Builder()
                            .url(WebServerEndpoint + "playlist" + "?id=" + playlistId)
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
