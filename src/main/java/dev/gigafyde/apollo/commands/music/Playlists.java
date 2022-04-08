package dev.gigafyde.apollo.commands.music;

/*
 Created by SyntaxDragon
  https://github.com/SyntaxDragon
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lavalink.client.LavalinkUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Playlists extends Command {
    private static final Logger log = LoggerFactory.getLogger("Playlists");
    private TrackScheduler scheduler;
    private CommandEvent event;

    public Playlists() {
        this.name = "playlists";
        this.triggers = new String[]{"playlists", "playlist", "pl"};
        this.guildOnly = true;
    }

    public static void loadSharePlaylist(String url, TrackScheduler scheduler, CommandEvent event) {
        try {
            // if bot is not in VC join the VC
            AudioChannel vc = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
            assert vc != null;
            scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
            if (scheduler == null) scheduler = event.getClient().getMusicManager().addScheduler(vc, false);

            // build client and request
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(new Request.Builder()
                    .url(url + "?raw=true").build()).execute();
            // if playlists exist
            if (response.isSuccessful()) {
                JSONObject playlist = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray tracks = playlist.getJSONArray("tracks");
                String name = playlist.getString("name");
                scheduler.addTracks("Playlist", decodeTracks(tracks));
                event.send(String.format("Loaded playlist `%s` with `%s` tracks.", name, tracks.length()));
                // if no playlists exist / an error happened
            } else {
                JSONObject error = new JSONObject(Objects.requireNonNull(response.body()).string());
                String message = error.getString("message");
                event.sendError(String.format("**Playlists Error: %s**", message));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // decode JSON Array into queue tracks
    private static List<AudioTrack> decodeTracks(JSONArray identifiers) {
        List<AudioTrack> tracks = new ArrayList<>();
        for (Object identifier : identifiers) {
            try {
                tracks.add(LavalinkUtil.toAudioTrack((String) identifier));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return tracks;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));

        switch (event.getCommandType()) {
            case MESSAGE -> {
                String[] args = event.getArgument().split(" ", 3);
                // 0 = command, 1 = name, 2 = page

                // if command has no args return a reminder of what commands exist
                if (event.getArgument().isEmpty()) {
                    event.sendError("**" + Main.BOT_PREFIX + "playlists <save|update|add|load|delete|list|share> <name>**");
                    return;
                }
                // parse what sub command is being used here
                switch (args[0]) {
                    case "save" -> {
                        // if no name is provided
                        if (args.length == 1) {
                            event.sendError("**" + Main.BOT_PREFIX + "playlists save <name>**");
                            return;
                        }
                        createPlaylist(args[1], event.getAuthor().getId());
                        return;
                    }
                    case "add" -> {
                        // if no name is provided
                        if (args.length == 1) {
                            event.sendError("**" + Main.BOT_PREFIX + "playlists add <name>**");
                            return;
                        }
                        addPlaylist(args[1], event.getAuthor().getId());
                        return;
                    }
                    case "load" -> {
                        // if no name is provided
                        if (args.length == 1) {
                            event.sendError("**" + Main.BOT_PREFIX + "playlists load <name>**");
                            return;
                        }
                        loadPlaylist(args[1], event.getAuthor().getId());
                        return;
                    }
                    case "update" -> {
                        // if no name is provided
                        if (args.length == 1) {
                            event.sendError("**" + Main.BOT_PREFIX + "playlists update <name>**");
                            return;
                        }
                        updatePlaylist(args[1], event.getAuthor().getId());
                        return;
                    }
                    case "share" -> {
                        // if no name is provided
                        if (args.length == 1) {
                            event.sendError("**" + Main.BOT_PREFIX + "playlists share <name>**");
                            return;
                        }
                        sharePlaylist(args[1], event.getAuthor().getId());
                        return;
                    }

                    case "list" -> {
                        if (args.length != 1) {
                            try {
                                listPlaylists(event.getAuthor().getName(), event.getAuthor().getId(), Integer.parseInt(args[1]));
                                return;
                            } catch (Exception e) {
                                event.sendError(Constants.invalidInt);
                                return;
                            }
                        }
                        listPlaylists(event.getAuthor().getName(), event.getAuthor().getId(), 0);
                        return;
                    }
                    case "delete" -> {
                        // if no name is provided
                        if (args.length == 1) {
                            event.sendError("**" + Main.BOT_PREFIX + "playlists delete <name>**");
                            return;
                        }
                        deletePlaylist(args[1], event.getAuthor().getId());
                        return;
                    }
                }
                event.sendError("**Unable to find playlist command  " + args[0] + "**");
            }
            case SLASH -> {
                switch (Objects.requireNonNull(event.getSubcommandName())) {
                    case "save" -> createPlaylist(Objects.requireNonNull(event.getOption("name")).getAsString(), event.getAuthor().getId());
                    case "load" -> loadPlaylist(Objects.requireNonNull(event.getOption("name")).getAsString(), event.getAuthor().getId());
                    case "update" -> updatePlaylist(Objects.requireNonNull(event.getOption("name")).getAsString(), event.getAuthor().getId());
                    case "add" -> addPlaylist(Objects.requireNonNull(event.getOption("name")).getAsString(), event.getAuthor().getId());
                    case "share" -> sharePlaylist(Objects.requireNonNull(event.getOption("name")).getAsString(), event.getAuthor().getId());
                    case "list" -> {
                        if (!event.getOptions().isEmpty()) {
                            try {
                                listPlaylists(event.getAuthor().getName(), event.getAuthor().getId(), Integer.parseInt(Objects.requireNonNull(event.getOption("page")).getAsString()));
                            } catch (Exception e) {
                                event.sendError(Constants.invalidInt);
                            }
                        } else {
                            listPlaylists(event.getAuthor().getName(), event.getAuthor().getId(), 0);
                        }
                    }
                    case "delete" -> deletePlaylist(Objects.requireNonNull(event.getOption("name")).getAsString(), event.getAuthor().getId());
                }
            }
        }

    }

    private void createPlaylist(String name, String user_id) {
        // if bot isn't being used
        if (scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        // create tracks list
        List<AudioTrack> tracks = new ArrayList<>();
        // add current playing track
        tracks.add(scheduler.getPlayer().getPlayingTrack());
        // add the queue
        tracks.addAll(scheduler.getQueue());
        // turn it into an array list
        tracks = new ArrayList<>(tracks);
        // set json header
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // make json object
        JSONArray convertedTracks = convertTracks(tracks);
        JSONObject jsonObject = new JSONObject()
                .put("tracks", convertedTracks)
                .put("user_id", user_id)
                .put("name", name);
        // make body
        RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON);
        try {
            Response response = Main.httpClient.newCall(new Request.Builder()
                    .url(Main.PLAYLISTS_WEB_SERVER + "create" + "?key=" + Main.PLAYLISTS_API_KEY)
                    .post(body).build()).execute();
            if (response.isSuccessful()) {
                event.send(String.format("Created playlist `%s` with `%s` tracks.", name, convertedTracks.length()));
            } else {
                JSONObject error = new JSONObject(Objects.requireNonNull(response.body()).string());
                String message = error.getString("message");
                event.sendError(String.format("**Playlists Error: %s**", message));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void loadPlaylist(String name, String user_id) {
        try {
            if (!SongUtils.passedVoiceChannelChecks(event)) return;
            // if bot is not in VC join the VC
            AudioChannel vc = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
            assert vc != null;
            scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
            if (scheduler == null) scheduler = event.getClient().getMusicManager().addScheduler(vc, false);

            // build client and request
            Response response = Main.httpClient.newCall(new Request.Builder()
                    .url(Main.PLAYLISTS_WEB_SERVER + "get" + "?key=" + Main.PLAYLISTS_API_KEY + "&user_id=" + user_id + "&name=" + name).build()).execute();
            // if playlists exist
            if (response.isSuccessful()) {
                JSONObject playlist = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray tracks = playlist.getJSONArray("tracks");
                scheduler.addTracks("Playlist", decodeTracks(tracks));
                event.send(String.format("Loaded playlist `%s` with `%s` tracks.", name, tracks.length()));
                // if no playlists exist / an error happened
            } else {
                JSONObject error = new JSONObject(Objects.requireNonNull(response.body()).string());
                String message = error.getString("message");
                event.sendError(String.format("**Playlists Error: %s**", message));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private void sharePlaylist(String name, String user_id) {
        try {
            // build client and request
            Response response = Main.httpClient.newCall(new Request.Builder()
                    .url(Main.PLAYLISTS_WEB_SERVER + "get" + "?key=" + Main.PLAYLISTS_API_KEY + "&user_id=" + user_id + "&name=" + name).build()).execute();
            // if playlists exist
            if (response.isSuccessful()) {
                JSONObject playlist = new JSONObject(Objects.requireNonNull(response.body()).string());
                String url = playlist.getString("_id");
                event.send(Main.PLAYLISTS_WEB_SERVER + "share/" + url);
                // if no playlists exist / an error happened
            } else {
                JSONObject error = new JSONObject(Objects.requireNonNull(response.body()).string());
                String message = error.getString("message");
                event.sendError(String.format("**Playlists Error: %s**", message));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private void updatePlaylist(String name, String user_id) {
        if (scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        // create tracks list
        List<AudioTrack> tracks = new ArrayList<>();
        // add current playing track
        tracks.add(scheduler.getPlayer().getPlayingTrack());
        // add the queue
        tracks.addAll(scheduler.getQueue());
        // turn it into an array list
        tracks = new ArrayList<>(tracks);
        // set json header
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // make json object
        JSONArray convertedTracks = convertTracks(tracks);
        JSONObject jsonObject = new JSONObject()
                .put("tracks", convertedTracks)
                .put("user_id", user_id)
                .put("name", name);
        // make body
        RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON);
        try {
            Response response = Main.httpClient.newCall(new Request.Builder()
                    .url(Main.PLAYLISTS_WEB_SERVER + "update" + "?key=" + Main.PLAYLISTS_API_KEY)
                    .post(body).build()).execute();
            if (response.isSuccessful()) {
                event.send(String.format("Updated playlist `%s` now containing `%s` tracks.", name, convertedTracks.length()));
            } else {
                JSONObject error = new JSONObject(Objects.requireNonNull(response.body()).string());
                String message = error.getString("message");
                event.sendError(String.format("**Playlists Error: %s**", message));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void addPlaylist(String name, String user_id) {
        if (scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        // create tracks list
        List<AudioTrack> track = new ArrayList<>();
        // add current playing track
        track.add(scheduler.getPlayer().getPlayingTrack());
        // turn it into an array list
        track = new ArrayList<>(track);
        // set json header
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // make json object
        JSONArray convertedTrack = convertTracks(track);
        JSONObject jsonObject = new JSONObject()
                .put("track", convertedTrack)
                .put("user_id", user_id)
                .put("name", name);
        // make body
        RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON);
        try {
            Response response = Main.httpClient.newCall(new Request.Builder()
                    .url(Main.PLAYLISTS_WEB_SERVER + "add" + "?key=" + Main.PLAYLISTS_API_KEY)
                    .post(body).build()).execute();
            if (response.isSuccessful()) {
                event.send(String.format("Added song to playlist `%s`.", name));
            } else {
                JSONObject error = new JSONObject(Objects.requireNonNull(response.body()).string());
                String message = error.getString("message");
                event.sendError(String.format("**Playlists Error: %s**", message));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    private void deletePlaylist(String name, String user_id) {
        // set json header
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // make json object
        JSONObject jsonObject = new JSONObject()
                .put("user_id", user_id)
                .put("name", name);
        // make body
        RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON);
        try {
            Response response = Main.httpClient.newCall(new Request.Builder()
                    .url(Main.PLAYLISTS_WEB_SERVER + "delete" + "?key=" + Main.PLAYLISTS_API_KEY)
                    .post(body).build()).execute();
            if (response.isSuccessful()) {
                event.send(String.format("Deleted playlist `%s`", name));
            } else {
                JSONObject error = new JSONObject(Objects.requireNonNull(response.body()).string());
                String message = error.getString("message");
                event.sendError(String.format("**Playlists Error: %s**", message));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void listPlaylists(String username, String user_id, Integer page) {
        try {
            // build client and request
            Response response = Main.httpClient.newCall(new Request.Builder()
                    .url(Main.PLAYLISTS_WEB_SERVER + "list" + "?key=" + Main.PLAYLISTS_API_KEY + "&user_id=" + user_id).build()).execute();
            // if it worked
            if (response.isSuccessful()) {
                JSONArray playlists = new JSONArray(Objects.requireNonNull(response.body()).string());
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(username + "'s Saved Playlists");


                if (page < 1) page = 1;
                int maxPages = (playlists.length() / 10) + 1;

                if (page > maxPages) page = maxPages;
                int lowerLimit = (page - 1) * 10;
                int higherLimit = lowerLimit + 10;
                if (higherLimit > playlists.length()) higherLimit = playlists.length();

                ArrayList<String> playlistArray = new ArrayList<>();
                playlistArray.add("```nim\n");
                for (int i = lowerLimit; i < higherLimit; i++) {
                    JSONObject playlist = playlists.getJSONObject(i);
                    playlistArray.add(String.format("%d) %s\n", i + 1, playlist.get("name")));
                }
                playlistArray.add("```");
                eb.setDescription(String.join(" ", playlistArray));
                eb.setFooter("Page " + page + " of " + maxPages, null);
                event.sendEmbed(eb);
            }
            // if it fails
            else {
                JSONObject error = new JSONObject(Objects.requireNonNull(response.body()).string());
                String message = error.getString("message");
                event.sendError(String.format("**Playlists Error: %s**", message));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // convert current queue into JSON Array
    private JSONArray convertTracks(List<AudioTrack> songs) {
        JSONArray tracks = new JSONArray();
        songs.forEach(track -> {
            try {
                tracks.put(LavalinkUtil.toMessage(track));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        return tracks;
    }
}
