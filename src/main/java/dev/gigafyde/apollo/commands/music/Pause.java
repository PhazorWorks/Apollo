package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.SongUtils;

public class Pause extends Command {
    public Pause() {
        this.name = "pause";
        this.triggers = new String[]{"pause", "stop"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (track == null) {
            event.getMessage().reply("**Nothing is currently playing.**").mentionRepliedUser(true).queue();
            return;
        }
        scheduler.getPlayer().setPaused(true);
        event.getMessage().reply("**Paused at: `" + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack()) + "`**").mentionRepliedUser(false).queue();
    }

    protected void executeSlash(SlashEvent event) {

    }
}
