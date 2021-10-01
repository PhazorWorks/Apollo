package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import java.awt.Color;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lyrics extends Command {

    private static final Logger log = LoggerFactory.getLogger(Lyrics.class);
    private static Message message;
    private static InteractionHook hook;
    private static boolean slash;

    public Lyrics() {
        this.name = "lyrics";
        this.description = "Shows lyrics of currently playing song";
        this.triggers = new String[]{"lyrics"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        slash = false;
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
            sendEmbed(song);
        } else {
            String query = sendRequest(event.getArgument());
            if (query == null) return;
            JSONObject song = new JSONObject(query);
            sendEmbed(song);
        }
    }

    private void sendEmbed(JSONObject song) {
        String title = String.format("%s - %s", song.getString("artist"), song.getString("name"));
        String lyrics = song.getString("lyrics");
        Color blue = Color.decode("#4c87c2");
        EmbedBuilder embed = new EmbedBuilder();
        if (!slash) {
            if (lyrics.length() > 2000) {
                List<MessageEmbed> embeds = splitLyrics(lyrics, blue, title);
                if (lyrics.length() > 6000) {
                    for (MessageEmbed messageEmbed : embeds) {
                        message.getChannel().sendMessageEmbeds(messageEmbed).queue();
                    }
                } else {
                    message.getChannel().sendMessageEmbeds(embeds).queue();
                }
            } else {
                message.replyEmbeds(embed.setDescription(lyrics).setColor(blue).setTitle(title).build()).mentionRepliedUser(false).queue();
            }
        } else {
            if (lyrics.length() > 2000) {
                List<MessageEmbed> embeds = splitLyrics(lyrics, blue, title);
                if (lyrics.length() > 6000) {
                    for (MessageEmbed messageEmbed : embeds) {
                        hook.getInteraction().getMessageChannel().sendMessageEmbeds(messageEmbed).queue();
                    }
                    hook.editOriginal("**Lyrics**: ").queue();
                } else {
                    hook.editOriginalEmbeds(embeds).queue();
                }
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
        int page = 0;
        while (content.length() > 2048) {
            int index = content.lastIndexOf("", 2048);
            page++;
            embeds.add(embed.setDescription(content.substring(0, index).trim()).setColor(blue).setTitle(title).setFooter("Page " + page).build());
            content = content.substring(index).trim();
        }
        page++;
        embeds.add(embed.setDescription(content).setColor(blue).setTitle(title).setFooter("Page " + page).build());
        return embeds;
    }

    private String sendRequest(String title) {

        try {
            Response response = Main.httpClient.newCall(new Request.Builder().url(Main.LYRICS_WEB_SERVER + "?q=" + URLEncoder.encode(title, StandardCharsets.UTF_8) + "&key=" + Main.LYRICS_API_KEY).build()).execute();
            if (response.isSuccessful()) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                sendError(String.valueOf(response.code()));
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private void sendError(String error) {
        if (!slash) {
            message.reply("Lyrics Lookup failed! Aborting!").queue();
        } else {
            hook.editOriginal("Lyrics Lookup failed! Aborting!").queue();
        }
        log.debug("Lyrics lookup failed with error code: " + error);
    }

}
