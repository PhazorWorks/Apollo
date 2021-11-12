package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import okhttp3.*;
import org.json.JSONObject;

import java.io.InputStream;

public class NowPlaying extends Command {

    private CommandEvent event;

    public NowPlaying() {
        this.name = "nowplaying";
        this.triggers = new String[]{"np", "current", "now-playing"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;

        switch (event.getCommandType()) {
            case REGULAR -> {
                nowPlaying();
            }
            case SLASH -> {
                event.deferReply().queue();
                nowPlaying();
            }
        }
    }

    protected void nowPlaying() {
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (track == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject().put("title", track.getInfo().title).put("position", event.getClient().getMusicManager().getScheduler(event.getGuild()).getPlayer().getTrackPosition()).put("duration", track.getDuration());
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON); // new
            Response response = client.newCall(
                    new Request.Builder()
                            .url(System.getenv(Main.IMAGE_API_SERVER) + "np")
                            .post(body)
                            .build()).execute();
            InputStream inputStream = response.body().byteStream();
            event.sendFile(inputStream, "song.png");
        } catch (Exception e) {
            event.sendError("**Something went wrong trying to generate the image. " + e + "**");
            event.send(track.getInfo().author + " - " + track.getInfo().title + " - " + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer()));
        }
    }
}
