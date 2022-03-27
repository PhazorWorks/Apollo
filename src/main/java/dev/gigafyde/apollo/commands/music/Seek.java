package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lavalink.client.player.LavalinkPlayer;

public class Seek extends Command {

    private Integer seekNumber;
    private CommandEvent event;

    public Seek() {
        this.name = "seek";
        this.triggers = new String[]{"seek"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;

        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                seek();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                seek();
            }
        }
    }

    protected void seek() {
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        AudioTrack playingTrack = player.getPlayingTrack();
        if (playingTrack == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        if (!SongUtils.userConnectedToBotVC(event)) return;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (event.getArgument().isEmpty()) {
                    event.sendError(Constants.invalidInt);
                    return;
                }
                seekNumber = Integer.parseInt(event.getArgument());
            }
            case SLASH -> seekNumber = Integer.parseInt(Objects.requireNonNull(event.getOption("amount")).getAsString());
        }
        try {
            long maxSeekLength = playingTrack.getDuration();
            long amountToSeek = seekNumber * 1000L;
            long currentTime = player.getTrackPosition();
            if (amountToSeek + currentTime >= maxSeekLength) {
                event.sendError("**Input is larger then song duration!**");
                return;
            }
            long newTime = currentTime + amountToSeek;
            player.seekTo(newTime);
            if (event.getArgument().startsWith("-")) {
                long amountRewound = -amountToSeek;
                if (amountToSeek < 61000)
                    event.send(String.format("Rewound %d seconds", TimeUnit.MILLISECONDS.toSeconds(amountRewound) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountRewound))));
                else
                    event.send(String.format("Rewound %d min, %d seconds", TimeUnit.MILLISECONDS.toMinutes(amountRewound), TimeUnit.MILLISECONDS.toSeconds(amountRewound) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountRewound))));
            } else {
                if (amountToSeek < 61000)
                    event.send(String.format("%d seconds skipped", TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek))));
                else
                    event.send(String.format("%d min, %d seconds skipped", TimeUnit.MILLISECONDS.toMinutes(amountToSeek), TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek))));
            }
        } catch (NumberFormatException exception) {
            event.sendError(Constants.invalidInt);
        }
    }
}

