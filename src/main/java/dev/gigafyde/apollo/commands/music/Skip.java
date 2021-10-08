package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Skip extends Command {
    private Message message;
    private InteractionHook hook;
    private CommandEvent event;
    private TrackScheduler scheduler;
    private int tracks;

    public Skip() {
        this.name = "skip";
        this.triggers = new String[]{"skip", "s"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) {
                    sendError("**Nothing is currently playing**");
                    return;
                }
                skip();
            }
            case SLASH -> {
                event.deferReply().queue();
                hook = event.getHook();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) {
                    sendError("**Nothing is currently playing**");
                    return;
                }
                skip();
            }
        }

    }

    protected void skip() {
        args();
        if (tracks == 0) scheduler.skip();
        else scheduler.skip(tracks);
        if (scheduler.isLooped()) {
            scheduler.setLooped(false);
            send("Loop was turned off due to manual skip");
        }
        switch (event.getCommandType()) {
            case REGULAR -> message.addReaction(Emoji.SUCCESS.toString()).queue();
            case SLASH -> send("Skipped!");
        }
    }

    protected void args() {
        switch (event.getCommandType()) {
            case REGULAR -> {
                if (event.getArgument().isEmpty()) tracks = 0;
                else try {
                    tracks = Integer.parseInt(event.getArgument());
                } catch (Exception e) {
                    sendError("**Please send a number!**");
                }
            }
            case SLASH -> {
                try {
                    tracks = Integer.parseInt(event.getOption("tracks").getAsString());
                } catch (Exception e) {
                    tracks = 0;
                }
            }
        }
        tracks = 0;
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
