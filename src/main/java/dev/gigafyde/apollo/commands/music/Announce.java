package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Objects;

public class Announce extends Command {
    private CommandEvent event;

    public Announce() {
        this.name = "announce";
        this.triggers = new String[]{"announce"};

    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                announce(event.getArgument());
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                announce(event.getSubcommandName());
            }
        }
    }

    protected void announce(String option) {
        try {
            TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
            if (scheduler == null) {
                event.sendError(Constants.botNotInVC);
                return;
            }
            if ("tracks".equals(option)) {
                if (scheduler.isAnnounceTrack()) {
                    scheduler.setAnnounceTrack(false);
                    event.send("Announcing of **tracks** is now **disabled**");
                } else {
                    event.send("Announcing of **tracks** is now **enabled**");
                    scheduler.setAnnounceTrack(true);
                }
            } else if ("loop".equals(option)) {
                if (scheduler.isAnnounceLoop()) {
                    scheduler.setAnnounceLoop(false);
                    event.send("Announcing of **looped tracks** is now **disabled**");
                } else {
                    event.send("Announcing of **looped tracks** is now **enabled**");
                    scheduler.setAnnounceLoop(true);
                }
            } else {
                String enabledTracks = scheduler.isAnnounceTrack() ? "Enabled" : "Disabled";
                String enabledLoop = scheduler.isAnnounceLoop() ? "Enabled" : "Disabled";
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("Announcements Menu")
                        .setDescription(String.format("**__Description__**:\nManage if the bot should send a message when a new song is playing.\n\n**__Status__**:\n**Tracks** are currently **%s**\n**Looped Tracks** are currently **%s**\n\n**__How to use__**:\n%sannounce <tracks|loop>", enabledTracks, enabledLoop, event.getClient().getPrefix()));
                event.sendEmbed(eb);
            }
        } catch (Exception e) {
            event.sendError("**" + e.getMessage() + "**");
        }
    }
}
