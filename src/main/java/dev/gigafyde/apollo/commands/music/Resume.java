package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.command.messageCommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;

public class Resume extends Command {
    public Resume() {
        this.name = "resume";
        this.triggers = new String[]{"resume"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        scheduler.getPlayer().setPaused(false);
        event.getMessage().reply("**Resumed from: `" + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack()) + "`**").mentionRepliedUser(false).queue();
    }

    protected void executeSlash(SlashEvent event) {

    }

    @Override
    protected void executeContext(messageCommandEvent event) {

    }
}
