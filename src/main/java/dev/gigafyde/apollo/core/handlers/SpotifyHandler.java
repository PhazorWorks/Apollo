package dev.gigafyde.apollo.core.handlers;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.TrackScheduler;
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
            SongCallBackListener.notifySpotifyUnsupported();
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
            JSONObject artistObject = jsonObject.getJSONArray("artists").getJSONObject(0);
            String artist = artistObject.get("name").toString();
            String title = jsonObject.get("name").toString();
            String explicit = Boolean.parseBoolean(jsonObject.get("explicit").toString()) ? "explicit" : "";
            SongHandler.loadHandler(scheduler, artist + " " + title +  " " + explicit, true, true);
        } catch (Exception e) {
            SongCallBackListener.notifySpotifyAbort(e);
            log.error("Spotify Lookup failed! Aborting");
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
                SongHandler.loadPlaylistHandler(tracks, scheduler, artist + " " + title, true, true);
            }

//            event.getMessage().reply(String.format("**Added %s tracks from the playlist!**", tracks.length())).mentionRepliedUser(false).queue();
        } catch (Exception e) {
            SongCallBackListener.notifySpotifyAbort(e);
        }
    }

}
