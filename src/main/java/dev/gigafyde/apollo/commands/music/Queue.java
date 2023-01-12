package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.GuildMusicManager;
import dev.gigafyde.apollo.core.MusicManager;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;

public class Queue extends Command {

    private TrackScheduler scheduler;
    private CommandEvent event;


    public Queue() {
        this.name = "queue";
        this.triggers = new String[]{"q", "queue"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        GuildMusicManager musicManager = event.getClient().getMusicManager().getGuildMusicManager(event.getGuild());
        scheduler = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;


        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (scheduler == null) {
                    event.sendError(Constants.requireActivePlayerCommand);
                    return;
                }
                if (scheduler.getQueue().isEmpty()) {
                    event.send("**Queue is currently empty**");
                    return;
                }
                queue();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (scheduler == null) {
                    event.sendError(Constants.requireActivePlayerCommand);
                    return;
                }
                if (scheduler.getQueue().isEmpty()) {
                    event.sendError("**Queue is currently empty**");
                }
                queue();
            }
        }
    }


    protected void queue() {
        List<AudioTrack> queuedTracks = new ArrayList<>(scheduler.getQueue());
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
                case MESSAGE -> page = Integer.parseInt(event.getArgument());
                case SLASH -> {
                    try {
                        page = Integer.parseInt(Objects.requireNonNull(event.getOption("page")).getAsString());
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
        event.sendEmbed(eb);
    }
}
