package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;

import java.util.Objects;


public class Restart extends Command {
    private CommandEvent event;

    public Restart() {
        this.name = "restart";
        this.triggers = new String[]{"restart", "redo"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                restart();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                restart();
            }
        }
    }

    protected void restart() {
        AudioPlayer player = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).player;
        if (player.getPlayingTrack() == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        if (!SongUtils.userConnectedToBotVC(event)) return;
        player.getPlayingTrack().setPosition(0L);
        event.send("Replaying song " + player.getPlayingTrack().getInfo().title + ".");
    }
}
