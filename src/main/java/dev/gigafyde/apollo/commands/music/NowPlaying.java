package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.io.InputStream;
import java.util.Objects;

public class NowPlaying extends Command {

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
        TrackScheduler scheduler = event.getClient().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
        AudioTrack track = scheduler.getPlayer().getPlayingTrack();
        if (track == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        try {
            if (Main.USE_IMAGE_API) {
                InputStream inputStream = SongUtils.generateNowPlaying(track, track.getPosition());
                event.sendFile(inputStream, "song.png");
            } else {
                event.sendMessage(track.getInfo().author + " - " + track.getInfo().title + " - " + SongUtils.getSongProgress(scheduler.getPlayer()));
            }

        } catch (Exception e) {
            event.sendError("**Something went wrong trying to generate the image. " + e + "**");
            event.sendMessage(track.getInfo().author + " - " + track.getInfo().title + " - " + SongUtils.getSongProgress(scheduler.getPlayer()));
        }
    }
}
