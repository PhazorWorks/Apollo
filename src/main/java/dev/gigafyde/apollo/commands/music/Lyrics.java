package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Lyrics extends Command {

    private static final Logger log = LoggerFactory.getLogger("Lyrics");
    private static Message message;
    private static InteractionHook hook;

    public Lyrics() {
        this.name = "lyrics";
        this.description = "Shows lyrics of currently playing song";
        this.triggers = new String[]{"lyrics"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        String args = event.getArgument();
        message = event.getMessage();
        if (args.isEmpty()) {
            if (!SongUtils.passedVoiceChannelChecks(event)) return;
            AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
            if (track == null) {
                event.getMessage().reply("**Nothing is currently playing.**").mentionRepliedUser(true).queue();
                return;
            }
            String query = sendRequest(track.getInfo().title);
            if (query == null) return;
            JSONObject song = new JSONObject(query);
            send(song);
        } else {
            String query = sendRequest(event.getArgument());
            if (query == null) return;
            JSONObject song = new JSONObject(query);
            send(song);
        }
    }

    protected void executeSlash(SlashEvent event) {
        event.getSlashCommandEvent().deferReply(false).queue();
        hook = event.getSlashCommandEvent().getHook();
        if (event.getSlashCommandEvent().getOption("query") == null) {
            AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
            if (track == null) {
                hook.editOriginal("**Nothing is currently playing.**").queue();
                return;
            }
            String query = sendRequest(track.getInfo().title);
            if (query == null) return;
            JSONObject song = new JSONObject(query);
            send(song);
        } else {
            String query = sendRequest(event.getSlashCommandEvent().getOption("query").getAsString());
            if (query == null) return;
            JSONObject song = new JSONObject(query);
            send(song);
        }
    }

    private void send(JSONObject song) {
        String title = String.format("%s - %s", song.getString("artist"), song.getString("name"));
        String lyrics = song.getString("lyrics");
        Color blue = Color.decode("#4c87c2");
        EmbedBuilder embed = new EmbedBuilder();
        System.out.println(hook);
        if (hook == null) {
            if (lyrics.length() > 2000) {
                List<MessageEmbed> embeds = splitLyrics(lyrics, blue, title);
                message.getChannel().sendMessage(embeds.get(0)).queue();
            } else {
                message.reply(embed.setDescription(lyrics).setColor(blue).setTitle(title).build()).mentionRepliedUser(false).queue();
            }
        } else {
            if (lyrics.length() > 2000) {
                List<MessageEmbed> embeds = splitLyrics(lyrics, blue, title);
                hook.editOriginalEmbeds(embeds.get(0)).queue();
            } else {
                hook.editOriginalEmbeds(embed.setDescription(lyrics).setColor(blue).setTitle(title).build()).queue();
            }
            hook = null;
        }
    }

    private List<MessageEmbed> splitLyrics(String lyrics, Color blue, String title) {
        EmbedBuilder embed = new EmbedBuilder();
        List<MessageEmbed> embeds = new ArrayList<>();
        String content = lyrics.trim();
        while (content.length() > 2000) {
            int index = content.lastIndexOf("\n\n", 2000);
            if (index == -1)
                index = content.lastIndexOf("\n", 2000);
            if (index == -1)
                index = content.lastIndexOf(" ", 2000);
            if (index == -1)
                index = 2000;
            embeds.add(embed.setDescription(content.substring(0, index).trim()).setColor(blue).setTitle(title).build());
            content = content.substring(index).trim();
            embed.setAuthor(null).setTitle(null, null);
        }
        embeds.add(embed.setDescription(content).setColor(blue).setTitle(title).build());
        return embeds;
    }

    private String sendRequest(String title) {
        String web = System.getenv("LYRICS_WEB_SERVER");
        String key = System.getenv("LYRICS_API_KEY");
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(new Request.Builder().url(web + "?q=" + URLEncoder.encode(title, StandardCharsets.UTF_8) + "&key=" + key).build()).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                sendError(response);
                return null;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void sendError(Response response) {
        if (hook == null) {
            message.reply("Lyrics Lookup failed! Aborting!").queue();
            System.out.println(String.format("Lyrics lookup failed with error code %s", response.code()));
        } else {
            hook.editOriginal("Lyrics Lookup failed! Aborting!").queue();
            System.out.println(String.format("Lyrics lookup failed with error code %s", response.code()));
        }
    }
}
