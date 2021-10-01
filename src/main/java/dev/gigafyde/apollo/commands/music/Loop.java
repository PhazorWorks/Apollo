package dev.gigafyde.apollo.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Loop extends Command {

    private Message message;
    private InteractionHook hook;
    private boolean slash = false;
    private boolean context = false;
    private CommandEvent event;

    public Loop() {
        this.name = "loop";
        this.triggers = new String[]{"loop"};

    }

    protected void execute(CommandEvent event) {
        this.event = event;
        slash = false;
        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                loop();
            }
            case SLASH -> {
                slash = true;
                hook = event.getHook();
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                loop();
            }
            case CONTEXT -> {
                context = true;
                slash = false;
            }
        }
    }

    protected void loop() {
        try {
            TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
            AudioTrack track = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack();
            if (scheduler == null | track == null) {
                sendError("**Nothing is currently playing! Queue some tracks first**");
                return;
            }
            if (!scheduler.isLooped()) {
                send("Loop is now enabled for the current track");
                scheduler.setLooped(true);
                scheduler.setLoopedSong(event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getPlayingTrack());
            } else {
                send("Loop is now disabled");
                scheduler.setLooped(false);
            }
        } catch (Exception e) {
            sendError(e.getMessage());
        }
    }

    protected void sendError(String error) {
        if (slash) hook.editOriginal(error).queue();
        else message.reply(error).mentionRepliedUser(true).queue();
    }

    protected void send(String content) {
        if (slash) hook.editOriginal(content).queue();
        else message.reply(content).mentionRepliedUser(true).queue();
    }
}
