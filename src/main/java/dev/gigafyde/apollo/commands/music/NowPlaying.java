package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.*;
import org.json.JSONObject;

import java.io.InputStream;

public class NowPlaying extends Command {

    private Message message;
    private InteractionHook hook;
    private boolean slash = false;
    private boolean context = false;
    private CommandEvent event;

    public NowPlaying() {
        this.name = "nowplaying";
        this.triggers = new String[]{"np", "current", "now-playing"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;

        switch (event.getCommandType()) {
            case REGULAR -> {
                slash = false;
                message = event.getMessage();
                nowPlaying();
            }
            case SLASH -> {
                event.deferReply().queue();
                slash = true;
                hook = event.getHook();
                nowPlaying();
            }
            case CONTEXT -> {
                slash = false;
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
        } catch (Exception ignored) {
            sendError("Something went wrong trying to generate the image.");
        }
    }

    protected void sendError(String error) {
        if (slash) hook.editOriginal(error).queue();
        else message.reply(error).mentionRepliedUser(true).queue();
    }

    protected void send(InputStream inputStream, String name) {
        if (slash) hook.editOriginal(inputStream, name).queue();
        else message.reply(inputStream, name).queue();
    }
}
