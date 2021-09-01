package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.concurrent.TimeUnit;
import lavalink.client.player.IPlayer;

public class Seek extends Command {
    public Seek() {
        this.name = "seek";
        this.triggers = new String[]{"seek"};
    }

    public void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        IPlayer player = event.getClient().getMusicManager().getScheduler(event.getGuild()).getPlayer();
        String argument = event.getArgument();
        if (argument.isEmpty()) {
            event.getTrigger().reply("Please provide a new position in minutes.").queue();
            return;
        }
        try {
            int seekNumber = Integer.parseInt(argument);
            AudioTrack playingTrack = player.getPlayingTrack();
            long maxSeekLength = playingTrack.getDuration();
            long amountToSeek = seekNumber * 1000L;
            if (amountToSeek > maxSeekLength) {
                event.getTrigger().reply("").queue();
                return;
            }
            long newTime = player.getPlayingTrack().getPosition() + amountToSeek;
            player.seekTo(newTime);
            if (event.getArgument().startsWith("-")) {
                long amountRewound = -amountToSeek;
                if (amountToSeek < 61000)
                    event.getTrigger().reply(String.format("** Rewound %d seconds**", TimeUnit.MILLISECONDS.toSeconds(amountRewound) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountRewound)))).queue();
                else
                    event.getTrigger().reply(String.format("** Rewound %d min, %d seconds**", TimeUnit.MILLISECONDS.toMinutes(amountRewound), TimeUnit.MILLISECONDS.toSeconds(amountRewound) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountRewound)))).queue();
            } else {
                if (amountToSeek < 61000)
                    event.getTrigger().reply(String.format("** %d seconds skipped**", TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek)))).queue();
                else
                    event.getTrigger().reply(String.format("** %d min, %d seconds skipped**", TimeUnit.MILLISECONDS.toMinutes(amountToSeek), TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek)))).queue();
            }
        } catch (NumberFormatException exception) {
            event.getTrigger().reply("Failed to parse number from input").queue();
        }
    }
}
