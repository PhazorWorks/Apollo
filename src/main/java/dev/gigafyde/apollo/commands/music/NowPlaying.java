package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.*;
import org.json.JSONObject;

import java.io.InputStream;

public class NowPlaying extends Command {

    private Message message;
    private InteractionHook hook;
    private CommandEvent event;

    public NowPlaying() {
        this.name = "nowplaying";
        this.triggers = new String[]{"np", "current", "now-playing"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;

        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                nowPlaying();
            }
            case SLASH -> {
                event.deferReply().queue();
                hook = event.getHook();
                nowPlaying();
            }
        }
    }

    protected void nowPlaying() {
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (track == null) {
            sendError("**Nothing is currently playing.**");
            return;
        }
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject().put("title", track.getInfo().title).put("position", event.getClient().getMusicManager().getScheduler(event.getGuild()).getPlayer().getTrackPosition()).put("duration", track.getDuration());
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON); // new
            Response response = client.newCall(
                    new Request.Builder()
                            .url(System.getenv("IMAGE_API") + "np")
                            .post(body)
                            .build()).execute();
            InputStream inputStream = response.body().byteStream();
            send(inputStream, "song.png");
        } catch (Exception e) {
            sendError("Something went wrong trying to generate the image. " + e);
            send(track.getInfo().author + " - " + track.getInfo().title + " - " + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer()));
        }
    }

    protected void sendError(String error) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(error).mentionRepliedUser(false).queue();
            case SLASH -> hook.editOriginal(error).queue();
        }
    }

    protected void send(String content) {
        switch (event.getCommandType()){
            case REGULAR -> message.reply(content).mentionRepliedUser(true).queue();
            case SLASH -> hook.editOriginal(content).queue();
        }
    }

    protected void send(InputStream inputStream, String name) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(inputStream, name).mentionRepliedUser(false).queue();
            case SLASH -> hook.editOriginal(inputStream, name).queue();
        }
    }
}
