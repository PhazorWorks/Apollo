package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;

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
            AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
            if (scheduler == null | track == null) {
                event.sendError(Constants.requireActivePlayerCommand);
                return;
            }
            if (!SongUtils.userConnectedToBotVC(event)) return;
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
                event.sendError("**" + event.getClient().getPrefix() + "announce <tracks|loop>**");
            }
        } catch (Exception e) {
            event.sendError("**" + e.getMessage() + "**");
        }
    }
}
