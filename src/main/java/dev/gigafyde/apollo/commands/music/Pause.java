package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Pause extends Command {

    private Message message;
    private InteractionHook hook;
    private CommandEvent event;

    public Pause() {
        this.name = "pause";
        this.triggers = new String[]{"pause", "stop"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                pause();
            }
            case SLASH -> {
                hook = event.getHook();
                event.deferReply().queue();
                pause();
            }
        }
    }

    protected void pause() {
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (track == null) {
            sendError("**Nothing is currently playing!**");
            return;
        }
        scheduler.getPlayer().setPaused(true);
        send("Paused at: `" + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer()) + "`");
    }

    protected void sendError(String error) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(error).mentionRepliedUser(true).queue();
            case SLASH -> hook.editOriginal(error).queue();
        }
    }

    protected void send(String content) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(content).mentionRepliedUser(false).queue();
            case SLASH -> hook.editOriginal(content).queue();
        }
    }
}
