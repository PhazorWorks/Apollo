package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

public class Loop extends Command {
    private CommandEvent event;

    public Loop() {
        this.name = "loop";
        this.triggers = new String[]{"loop"};

    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                loop();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                loop();
            }
        }
    }

    protected void loop() {
        try {
            TrackScheduler scheduler = event.getClient().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;

            if (!SongUtils.userConnectedToBotVC(event)) return;
            if (!scheduler.isRepeating()) {
                event.sendMessage("Loop is now enabled for the current track.");
                scheduler.setRepeat(true);
            } else {
                event.sendMessage("Loop is now disabled.");
                scheduler.setRepeat(false);
            }
        } catch (Exception e) {
            event.sendError("**" + e.getMessage() + "**");
        }
    }
}
