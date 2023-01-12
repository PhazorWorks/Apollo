package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

public class Link extends Command {
    public Link() {
        this.name = "link";
        this.triggers = new String[]{"link"};
        this.description = "Displays the url to the currently playing track";
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        TrackScheduler scheduler = event.getClient().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).scheduler;
        if (scheduler == null || scheduler.getPlayer().getPlayingTrack() == null) {
            event.sendError(Constants.requireActivePlayerCommand);
            return;
        }
        switch (event.getCommandType()) {
            case MESSAGE -> event.getMessage().reply("Currently playing: " + scheduler.getPlayer().getPlayingTrack().getInfo().uri).queue();
            case SLASH -> event.getHook().editOriginal("Currently playing: " + scheduler.getPlayer().getPlayingTrack().getInfo().uri).queue();
        }
    }
}
