package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
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
        if (scheduler == null) scheduler = event.getClient().getMusicManager().addScheduler(event.getMember().getVoiceState().getChannel(), false);
        scheduler.getPlayer().setPaused(false);
        event.getMessage().reply("**Resumed from: `" + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer()) + "`**").mentionRepliedUser(false).queue();
    }
}
