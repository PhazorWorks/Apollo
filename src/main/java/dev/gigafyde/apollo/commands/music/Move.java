package dev.gigafyde.apollo.commands.music;

/*
 Created by SyntaxDragon
  https://github.com/SyntaxDragon
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

public class Move extends Command {
    private CommandEvent event;
    private TrackScheduler scheduler;

    public Move() {
        this.name = "move";
        this.triggers = new String[]{"move"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
                String[] args = event.getArgument().split(" ", 2);

                try {
                    int pos1 = Integer.parseInt(args[0]);
                    int pos2 = Integer.parseInt(args[1]);
                    move(pos1, pos2);
                } catch (Exception e) {
                    event.sendError(Constants.invalidInt);
                }
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
                try {
                    int pos1 = Integer.parseInt(Objects.requireNonNull(event.getOption("track")).getAsString());
                    int pos2 = Integer.parseInt(Objects.requireNonNull(event.getOption("position")).getAsString());
                    move(pos1, pos2);
                } catch (Exception e) {
                    event.sendError(Constants.invalidInt);
                }
            }
        }
    }

    protected void move(Integer pos1, Integer pos2) {
        if (scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        if (pos1 > scheduler.getQueue().size()) {
            event.sendError("**Position one is higher then the number of tracks in queue.**");
            return;
        }
        if (pos2 > scheduler.getQueue().size()) {
            event.sendError("**Position two is higher then the number of tracks in queue.**");
            return;
        }
        scheduler.moveTrack(pos1 - 1, pos2 - 1);
        switch (event.getCommandType()) {
            case MESSAGE, SLASH -> event.send(String.format("Moved **%s** from position `%d` to `%d`.", scheduler.getTrackTitleByPosition(pos2 - 1), pos1, pos2));
        }
    }

}
