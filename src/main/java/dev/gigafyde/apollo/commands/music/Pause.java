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
    private boolean slash = false;
    private boolean context = false;
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
                slash = false;
                message = event.getMessage();
                pause();
            }
            case SLASH -> {
                slash = true;
                hook = event.getHook();
                event.deferReply().queue();
                pause();
            }
            case CONTEXT -> {
                slash = false;
            }
        }
    }

    protected void pause() {
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (track == null) {
            sendError("**Nothing is currently playing.**");
            return;
        }
        scheduler.getPlayer().setPaused(true);
        send("**Paused at: `"  + SongUtils.getSongProgress(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer())  + "`**");
    }

    protected void sendError(String error) {
        if (slash) hook.editOriginal(error).queue();
        else message.reply(error).mentionRepliedUser(true).queue();
    }

    protected void send(String content) {
        if (slash) hook.editOriginal(content).queue();
        else message.reply(content).mentionRepliedUser(false).queue();
    }
}
