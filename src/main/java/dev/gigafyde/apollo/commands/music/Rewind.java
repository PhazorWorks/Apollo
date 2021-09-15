package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.concurrent.TimeUnit;

public class Rewind extends Command {
    private static Message message;
    private static InteractionHook hook;
    private static boolean slash;

    public Rewind() {
        this.name = "rewind";
        this.triggers = new String[]{"rewind"};
    }

    protected void execute(CommandEvent event) {
        slash = false;
        String args = event.getArgument();
        message = event.getMessage();
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        AudioTrack track = player.getPlayingTrack();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (track == null) {
            message.reply("**Nothing is currently playing.**").mentionRepliedUser(true).queue();
            return;
        }
        if (args.isEmpty()) {
            message.reply("Please provide a new position in seconds.").mentionRepliedUser(true).queue();
            return;
        }
        rewindSong(args, player);
    }
    protected void executeSlash(SlashEvent event) {
        slash = true;
        event.getSlashCommandEvent().deferReply(false).queue();
        hook = event.getSlashCommandEvent().getHook();
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        AudioTrack track = player.getPlayingTrack();
        String args = event.getSlashCommandEvent().getOption("amount").getAsString();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (track == null) {
            hook.editOriginal("**Nothing is currently playing.**").queue();
            return;
        }
        if (event.getSlashCommandEvent().getOption("amount") == null) {
            hook.editOriginal("Please provide a new position in seconds.").queue();
            return;
        }
        rewindSong(args, player);
    }
    private void rewindSong(String args, LavalinkPlayer player) {
        try {
            int seekNumber = Integer.parseInt(args);
            long amountToSeek = seekNumber * 1000L;
            long currentTime = player.getTrackPosition();
            long newTime = currentTime - amountToSeek;
            player.seekTo(newTime);
            if (!slash) {
                if (amountToSeek <= 59000)
                    message.reply(String.format("** Rewound %d seconds**", TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek)))).queue();
                else
                    message.reply(String.format("** Rewound %d min, %d seconds**", TimeUnit.MILLISECONDS.toMinutes(amountToSeek), TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek)))).queue();
            } else {
                if (amountToSeek <= 59000)
                    hook.editOriginal(String.format("** Rewound %d seconds**", TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek)))).queue();
                else
                    hook.editOriginal(String.format("** Rewound %d min, %d seconds**", TimeUnit.MILLISECONDS.toMinutes(amountToSeek), TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek)))).queue();
            }
        } catch (NumberFormatException exception) {
            if (!slash) message.reply("Failed to parse number from input").queue();
            else hook.editOriginal("Failed to parse number from input").queue();
        }
    }
}
