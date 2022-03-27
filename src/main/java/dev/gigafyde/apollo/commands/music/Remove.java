package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

public class Remove extends Command {
    private CommandEvent event;
    private TrackScheduler scheduler;
    private Integer numberToRemove;

    public Remove() {
        this.name = "remove";
        this.triggers = new String[]{"remove", "rm"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
                if (scheduler == null) {
                    event.sendError(Constants.requireActivePlayerCommand);
                    return;
                }
                remove();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
                if (scheduler == null) {
                    event.sendError(Constants.requireActivePlayerCommand);
                    return;
                }
                remove();
            }
        }


    }

    protected void remove() {
        try {
            if (!SongUtils.userConnectedToBotVC(event)) return;
            switch (event.getCommandType()) {
                case MESSAGE -> numberToRemove = Integer.parseInt(event.getArgument());
                case SLASH -> numberToRemove = Integer.parseInt(Objects.requireNonNull(event.getOption("input")).getAsString());
            }
            if (scheduler.getQueue().size() < numberToRemove) {
                event.sendError("**Number " + numberToRemove + " is not in the queue the you can only remove numbers 1 to " + scheduler.getQueue().size() + " !**");
                return;
            }
            if (numberToRemove <= 0) {
                event.sendError(Constants.numberBelowZero);
                return;
            }
            event.send(Emoji.SUCCESS + " Removed `" + scheduler.getTrackTitleByPosition(numberToRemove - 1) + "` from the queue.");
            scheduler.removeTrack(numberToRemove - 1);
        } catch (NumberFormatException exception) {
            event.sendError(Constants.invalidInt);
        }
    }
}
