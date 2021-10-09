package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;

public class Loop extends Command {
    private CommandEvent event;

    public Loop() {
        this.name = "loop";
        this.triggers = new String[]{"loop"};

    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
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
            TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
            AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
            if (scheduler == null | track == null) {
                event.sendError(Constants.requireActivePlayerCommand);
                return;
            }
            if (!SongUtils.userConnectedToBotVC(event)) return;
            if (!scheduler.isLooped()) {
                event.send("Loop is now enabled for the current track.");
                scheduler.setLooped(true);
                scheduler.setLoopedSong(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack());
            } else {
                event.send("Loop is now disabled.");
                scheduler.setLooped(false);
            }
        } catch (Exception e) {
            event.sendError("**" + e.getMessage() + "**");
        }
    }
}
