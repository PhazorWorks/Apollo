package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

public class Clear extends Command {

    private TrackScheduler scheduler;
    private CommandEvent event;

    public Clear() {
        this.name = "clear";
        this.triggers = new String[]{"clear", "cls", "clr"};
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
                clear();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
                if (scheduler == null) {
                    event.sendError(Constants.requireActivePlayerCommand);
                    return;
                }
                clear();
            }
        }
    }

    protected void clear() {
        if (SongUtils.userConnectedToBotVC(event)) {
            scheduler.getPlayer().stopTrack();
            scheduler.clear();
            scheduler.skip();
            event.send("The queue has been cleared.");
        }
    }
}
