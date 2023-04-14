package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;

import java.util.Objects;

public class Resume extends Command {
    private CommandEvent event;

    public Resume() {
        this.name = "resume";
        this.triggers = new String[]{"resume", "unpause"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                resume();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                resume();
            }
        }
    }

    protected void resume() {
        TrackScheduler scheduler = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
        if (scheduler == null || scheduler.getPlayer().getPlayingTrack() == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        if (!SongUtils.userConnectedToBotVC(event)) return;
        scheduler.getPlayer().setPaused(false);
        event.sendMessage("Resumed from: `" + SongUtils.getSongProgress(scheduler.getPlayer()) + "`");
    }
}
