package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import java.io.InputStream;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class NowPlaying extends Command {
    public NowPlaying() {
        this.name = "nowplaying";
        this.triggers = new String[]{"np"};
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        AudioTrack track = event.getClient().getMusicManager().getScheduler(event.getGuild()).getPlayer().getPlayingTrack();
        if (track == null) {
            channel.sendMessage("Nothing currently playing").queue();
            return;
        }
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject().put("title", track.getInfo().title).put("position", track.getPosition()).put("duration", track.getDuration());
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON); // new
            Response response = client.newCall(
                    new Request.Builder()
                            .url(System.getenv("IMAGE_API") + "/np")
                            .post(body)
                            .build()).execute();
            InputStream inputStream = response.body().byteStream();
            event.getTrigger().reply(inputStream, "test.png").mentionRepliedUser(false).queue();
        } catch (Exception ignored) {

        }
    }
}
