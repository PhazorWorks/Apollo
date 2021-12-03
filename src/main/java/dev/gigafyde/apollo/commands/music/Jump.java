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

public class Jump extends Command {
    private CommandEvent event;
    private TrackScheduler scheduler;
    private int tracks;
    public Jump() {
        this.name = "jump";
        this.triggers = new String[]{"jump", "skipto"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                try {
                    tracks = Integer.parseInt(event.getArgument()) - 1;
                } catch (Exception e) {
                    event.sendError(Constants.invalidInt);
                    return;
                }
                jump();
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                try {
                    tracks = Integer.parseInt(event.getOption("input").getAsString()) - 1;
                } catch (Exception e) {
                    event.sendError(Constants.invalidInt);
                    return;
                }
                jump();
            }
        }
    }

    protected void jump() {
        if (scheduler == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        if (tracks > scheduler.getQueue().size()) {
            event.sendError("**Input is higher then the number of tracks in queue.**");
            return;
        }
        scheduler.skip(tracks);
        scheduler.skip();
        if (scheduler.isLooped()) {
            scheduler.setLooped(false);
            event.send("Loop was turned off due to manual jump.");
        }
        switch (event.getCommandType()) {
            case REGULAR, SLASH -> event.send("Skipped to song `" + scheduler.getPlayer().getPlayingTrack().getInfo().title + "`.");
        }
    }

}