package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;

import java.util.Objects;

public class PlayBack extends Command {

    private TrackScheduler scheduler;
    private CommandEvent event;


    public PlayBack() {
        this.name = "playback";
        this.triggers = new String[]{"playback", "playprevious", "back", "previous"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        scheduler = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;

        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (scheduler == null) {
                    event.sendError(Constants.requireActivePlayerCommand);
                    return;
                }
                playback();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (scheduler == null) {
                    event.sendError(Constants.requireActivePlayerCommand);
                    return;
                }
                playback();
            }
        }
    }


    protected void playback() {
        AudioTrack previousSong = scheduler.getPreviousTrack();
        if (previousSong != null) {
            scheduler.addTopSong(previousSong);
            event.send(String.format("Added previous song `%s` to the queue!", previousSong.getInfo().title));
        } else {
            event.sendError("**There is no previous song to play!**");
        }
    }
}
