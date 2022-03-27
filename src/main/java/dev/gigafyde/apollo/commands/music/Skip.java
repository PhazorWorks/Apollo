package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

public class Skip extends Command {
    private CommandEvent event;
    private TrackScheduler scheduler;

    public Skip() {
        this.name = "skip";
        this.triggers = new String[]{"skip", "s", "n", "next"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
                skip();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild()));
                skip();
            }
        }
    }

    protected void skip() {
        if (scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        scheduler.skip();
        if (scheduler.isLooped()) {
            scheduler.setLooped(false);
            event.send("Loop was turned off due to manual skip.");
        }
        switch (event.getCommandType()) {
            case MESSAGE -> event.getMessage().addReaction(Emoji.SUCCESS.toString()).queue();
            case SLASH -> event.send("Skipped!");
        }
    }

}
