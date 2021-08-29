package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.MusicManager;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import dev.gigafyde.apollo.utils.TextUtils;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

public class Play extends Command {
    OkHttpClient client = new OkHttpClient();

    public Play() {
        this.name = "play";
        this.description = "Allows you to play a song of your choice";
        this.triggers = new String[]{"play", "p"};
        this.hidden = false;
        this.ownerOnly = false;
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
        if (scheduler == null) {
            VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
            if (vc == null) {
                event.getTrigger().reply("**Please join a voice channel first!**").mentionRepliedUser(true).queue();
                return;
            }
            try {
                scheduler = event.getClient().getMusicManager().addScheduler(vc, false);
            } catch (InsufficientPermissionException ignored) {
                event.getTrigger().reply("**Cannot join VC**").mentionRepliedUser(true).queue();
                return;
            }
        }
        if (event.getArgument().isEmpty()) {
            event.getTrigger().reply("Please provide a search query.").mentionRepliedUser(true).queue();
            return;
        }
        if (event.getArgument().contains("spotify")) {
            handleSpotify(event);
            return;
        }
        if (SongUtils.isValidURL(event.getArgument())) {
            String[] split = event.getArgument().split("&list=");
            loadHandler(event, scheduler, TextUtils.getStrippedSongUrl(split[0]), false);
        } else {
            loadHandler(event, scheduler, event.getArgument(), true);
        }
    }

    private void handleSpotify(CommandEvent event) {
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
        if (event.getArgument().contains("spotify")) {
            String[] argument = event.getArgument().split("/");
            String route = argument[3];
            if (!route.contains("track")) {
                event.getTrigger().reply("Invalid/Unsupported Spotify URL!").mentionRepliedUser(true).queue();
                return;
            }
            String object = argument[4];
            String[] objectId = object.split("\\?si");
            String WebServerEndpoint = System.getenv("SPOTIFY_WEB_SERVER");
            Response response;
            JSONObject jsonResponse;
            JSONObject jsonObject;
            try {
                response = client.newCall(
                        new Request.Builder()
                                .url(WebServerEndpoint + route + "?id=" + objectId[0])
                                .build()).execute();
                jsonResponse = new JSONObject(Objects.requireNonNull(response.body()).string());
                jsonObject = jsonResponse.getJSONObject("track");
            } catch (Exception e) {
                event.getTrigger().reply("Spotify Lookup failed! Aborting").mentionRepliedUser(true).queue();
                return;
            }
            if (route.equals("track")) {
                String artist = jsonObject.get("artist").toString();
                String title = jsonObject.get("name").toString();
                loadHandler(event, scheduler, artist + " " + title, true);
            }
        }
    }

    private void sendImage(CommandEvent event, AudioTrack track) {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject().put("title", track.getInfo().title).put("author", event.getAuthor().getName()).put("duration", track.getDuration()).put("uri", track.getInfo().uri).put("identifier", track.getInfo().identifier);
            RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON); // new
            Response response = client.newCall(
                    new Request.Builder()
                            .url(System.getenv("IMAGE_API") + "convert")
                            .post(body)
                            .build()).execute();
            InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
            event.getChannel().sendFile(inputStream, "thumbnail.png").queue();
            event.getTrigger().suppressEmbeds(true).queue();
        } catch (Exception ignored) {
            event.getTrigger().suppressEmbeds(true).queue();
            event.getTrigger().reply("Queued " + track.getInfo().title).mentionRepliedUser(false).queue();
        }
    }

    private void loadHandler(CommandEvent event, TrackScheduler scheduler, String searchQuery, boolean search) {
        scheduler.getManager().loadItem(search ? "ytsearch:" + searchQuery : searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (scheduler.addSong(track)) {
                    sendImage(event, track);
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
                    event.getTrigger().reply(String.format("**Added %s of %s from the playlist!**", added, playlist.getTracks().size())).mentionRepliedUser(false).queue();
                }
            }

            @Override
            public void noMatches() {
                if (search) {
                    event.getTrigger().reply("**Failed to find anything with the term: **" + event.getArgument()).mentionRepliedUser(true).queue();
                }
//                loadHandler(scheduler, event, true, user);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getTrigger().reply("**Failed to load track!**").mentionRepliedUser(false).queue();
                LoggerFactory.getLogger(Play.class).warn("**Couldn't load track:** " + exception);
            }
        });
    }
}
