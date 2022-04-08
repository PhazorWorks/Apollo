package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.io.InputStream;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NowPlaying extends Command {
    private static final Logger log = LoggerFactory.getLogger("NowPlaying");

    private CommandEvent event;

    public NowPlaying() {
        this.name = "nowplaying";
        this.triggers = new String[]{"np", "current", "now-playing"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;

        switch (event.getCommandType()) {
            case MESSAGE -> nowPlaying();
            case SLASH -> {
                event.deferReply().queue();
                nowPlaying();
            }
        }
    }

    protected void nowPlaying() {
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (track == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        try {
            InputStream inputStream = SongUtils.generateNowPlaying(track, event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild())).getPlayer().getTrackPosition());
            event.sendFile(inputStream, "song.png");
        } catch (Exception e) {
            event.sendError("**Something went wrong trying to generate the image. " + e + "**");
            event.send(track.getInfo().author + " - " + track.getInfo().title + " - " + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer()));
        }
    }
}
