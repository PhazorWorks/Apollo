package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Rewind extends Command {

    private Integer seekNumber;
    private CommandEvent event;

    public Rewind() {
        this.name = "rewind";
        this.triggers = new String[]{"rewind"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;

        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                rewind();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                rewind();
            }
        }
    }

    protected void rewind() {
        AudioPlayer player = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).player;
        AudioTrack track = player.getPlayingTrack();
        if (track == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        if (!SongUtils.userConnectedToBotVC(event)) return;
        try {
            switch (event.getCommandType()) {
                case MESSAGE -> seekNumber = Integer.parseInt(event.getArgument());
                case SLASH -> seekNumber = Integer.parseInt(Objects.requireNonNull(event.getOption("amount")).getAsString());
            }
            long amountToSeek = seekNumber * 1000L;
            long currentTime = player.getPlayingTrack().getPosition();
            long newTime = currentTime - amountToSeek;
            player.getPlayingTrack().setPosition(newTime);
            if (amountToSeek <= 59000)
                event.send(String.format("Rewound %d seconds.", TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek))));
            else
                event.send(String.format("Rewound %d min, %d seconds.", TimeUnit.MILLISECONDS.toMinutes(amountToSeek), TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek))));

        } catch (NumberFormatException exception) {
            event.sendError(Constants.invalidInt);
        }
    }

}
