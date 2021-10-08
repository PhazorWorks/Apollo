package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.MusicManager;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Queue extends Command {

    private TrackScheduler scheduler;
    private Message message;
    private InteractionHook hook;
    private CommandEvent event;


    public Queue() {
        this.name = "queue";
        this.triggers = new String[]{"q", "queue"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        MusicManager musicManager = event.getClient().getMusicManager();
        scheduler = musicManager.getScheduler(event.getGuild());

        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                if (scheduler == null)
                    sendError("**Nothing is currently playing!**");
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler.getQueue().isEmpty()) {
                    send("**Queue is currently empty**");
                    return;
                }
                queue();
            }
            case SLASH -> {
                hook = event.getHook();
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                if (scheduler == null) {
                    sendError("**Nothing is currently playing!**");
                    return;
                }
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler.getQueue().isEmpty()) {
                    send("**Queue is currently empty**");
                }
                queue();
            }
        }
    }


    protected void queue() {
        List<AudioTrack> queuedTracks = new ArrayList<>(scheduler.getQueue());
        if (scheduler.getPlayer().isPaused()) {
            return;
        }
        if (scheduler.getPlayer().getPlayingTrack().getInfo().uri.isEmpty()) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(event.getSelfMember().getColor());
        eb.setTitle("Currently playing:");
        eb.addField(scheduler.getPlayer().getPlayingTrack().getInfo().title, scheduler.getPlayer().getPlayingTrack().getInfo().uri, false);
        eb.addBlankField(false);

        int page = 1;
        try {
            switch (event.getCommandType()) {
                case REGULAR -> page = Integer.parseInt(event.getArgument());
                case SLASH -> {
                    try {
                        page = Integer.parseInt(event.getOption("page").getAsString());
                    } catch (Exception e) {
                        page = 0;
                    }
                }
            }
        } catch (NumberFormatException ignored) {
        }
        if (page < 1) page = 1;
        int maxPages = (queuedTracks.size() / 10) + 1;

        if (page > maxPages) page = maxPages;
        int lowerLimit = (page - 1) * 10;
        int higherLimit = lowerLimit + 10;
        if (higherLimit > queuedTracks.size()) higherLimit = queuedTracks.size();
        for (int i = lowerLimit; i < higherLimit; i++) {
            eb.addField(String.format("`[%d]` %s", i + 1, queuedTracks.get(i).getInfo().title), queuedTracks.get(i).getInfo().uri, false);
        }
        eb.setFooter("Page " + page + " of " + maxPages, null);
        sendEmbed(eb);
    }


    protected void sendError(String error) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(error).mentionRepliedUser(true).queue();
            case SLASH -> hook.editOriginal(error).queue();
        }
    }

    protected void send(String content) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(content).mentionRepliedUser(false).queue();
            case SLASH -> hook.editOriginal(content).queue();
        }
    }

    protected void sendEmbed(EmbedBuilder embed) {
        switch (event.getCommandType()) {
            case REGULAR -> event.getMessage().replyEmbeds(embed.build()).mentionRepliedUser(false).queue();
            case SLASH -> hook.editOriginalEmbeds(embed.build()).queue();
        }
    }
}
