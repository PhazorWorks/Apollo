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
import dev.gigafyde.apollo.utils.TextUtils;
import java.io.InputStream;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
    public Play() {
        this.name = "play";
        this.description = "Allows you to play a song of your choice";
        this.triggers = new String[]{"play", "p"};
        this.hidden = false;
        this.ownerOnly = false;
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
        if (scheduler == null) {
            VoiceChannel vc = event.getMember().getVoiceState().getChannel();
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
        loadHandler(scheduler, event, false, event.getAuthor());

    }


    private void loadHandler(TrackScheduler scheduler, CommandEvent event, boolean search, User user) {
        String[] split = event.getArgument().split("&list=");
        TextChannel channel = event.getTextChannel();
        if (event.getArgument().isEmpty()) {
            event.getTrigger().reply("Please provide a search query.").mentionRepliedUser(true).queue();
            return;
        }
        scheduler.getManager().loadItem(search ? "ytsearch:" + split[0] : TextUtils.getStrippedSongUrl(split[0]), new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {

                if (scheduler.addSong(track)) {
                    try {
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        JSONObject jsonObject = new JSONObject().put("title", track.getInfo().title).put("author", user.getName()).put("duration", track.getDuration()).put("uri", track.getInfo().uri).put("identifier", track.getInfo().identifier);
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON); // new
                        Response response = client.newCall(
                                new Request.Builder()
                                        .url(System.getenv("IMAGE_API") + "/convert")
                                        .post(body)
                                        .build()).execute();
                        InputStream inputStream = response.body().byteStream();
                        channel.sendFile(inputStream, "thumbnail.png").queue();
                        event.getTrigger().delete().queue();
                    } catch (Exception ignored) {
                        event.getTrigger().reply("Queued " + track.getInfo().title).mentionRepliedUser(false).queue();
                    }
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
                    return;
                }
                loadHandler(scheduler, event, true, user);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getTrigger().reply("**Failed to load track!**").mentionRepliedUser(false).queue();
                LoggerFactory.getLogger(Play.class).warn("**Couldn't load track:** " + exception);
            }
        });
    }
}
