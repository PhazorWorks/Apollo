package dev.gigafyde.apollo.commands.music;

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
                announce();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                announce();
            }
        }
    }

    protected void announce() {
        try {
            TrackScheduler scheduler = event.getClient().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
            if (scheduler == null) {
                event.sendError(Constants.botNotInVC);
                return;
            }
                if (scheduler.isSending()) {
                    scheduler.setSendPlaying(false);
                    event.send("Announcing of **tracks** is now **disabled**");
                } else {
                    event.send("Announcing of **tracks** is now **enabled**");
                    scheduler.setSendPlaying(true);
                }
        } catch (Exception e) {
            event.sendError("**" + e.getMessage() + "**");
        }
    }
}
