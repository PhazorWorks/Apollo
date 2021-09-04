package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.SongUtils;

public class Loop extends Command {
    public Loop() {
        this.name = "loop";
        this.triggers = new String[]{"loop"};

    }

    @Override
    protected void execute(CommandEvent event) {
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
//        if (scheduler == null | track == null) {
//            event.getMessage().reply("Nothing is currently playing! Queue some tracks first").queue();
//            return;
//        }
        if (!scheduler.isLooped()) {
            event.getMessage().reply("Loop is now enabled for the current track").mentionRepliedUser(false).queue();
            scheduler.setLooped(true);
            scheduler.setLoopedSong(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack());
        } else {
            event.getMessage().reply("Loop is now disabled").mentionRepliedUser(false).queue();
            scheduler.setLooped(false);
        }
    }

    @Override
    protected void executeSlash(SlashEvent event) {

    }
}
