package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class NowPlaying extends Command {
    public NowPlaying() {
        this.name = "nowplaying";
        this.triggers = new String[]{"np", "current"};
    }

    protected void execute(CommandEvent event) {
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (track == null) {
            event.getMessage().reply("**Nothing is currently playing.**").mentionRepliedUser(true).queue();
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
            event.getMessage().reply(inputStream, "test.png").mentionRepliedUser(false).queue();
        } catch (Exception ignored) {
            event.getMessage().reply("Something went wrong trying to generate the image.").queue();
        }
    }

    protected void executeSlash(SlashEvent event) {

    }
}
