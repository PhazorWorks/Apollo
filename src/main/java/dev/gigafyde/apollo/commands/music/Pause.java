package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

public class Pause extends Command {

    private CommandEvent event;

    public Pause() {
        this.name = "pause";
        this.triggers = new String[]{"pause", "stop"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> pause();
            case SLASH -> {
                event.deferReply().queue();
                pause();
            }
        }
    }

    protected void pause() {
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (!SongUtils.userConnectedToBotVC(event)) return;
        if (track == null || scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        scheduler.getPlayer().setPaused(true);
        event.send("Paused at: `" + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer()) + "`");
    }
}
