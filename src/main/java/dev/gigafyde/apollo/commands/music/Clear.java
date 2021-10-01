package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Clear extends Command {

    private TrackScheduler scheduler;
    private Message message;
    private InteractionHook hook;
    private boolean slash = false;
    private boolean context = false;
    private CommandEvent event;

    public Clear() {
        this.name = "clear";
        this.triggers = new String[]{"clear", "cls"};
    }

    protected void execute(CommandEvent event) {

        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) {
                    scheduler = makeScheduler();
                }
                clear(scheduler);
                send();
            }
            case SLASH -> {
                slash = true;
                hook = event.getHook();
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) {
                    scheduler = makeScheduler();
                }
                clear(scheduler);
                send();
            }
            case CONTEXT -> {
                context = true;
            }
        }
    }

    protected void clear(TrackScheduler scheduler) {
        scheduler.getQueue().clear();
        scheduler.skip();
    }

    protected TrackScheduler makeScheduler() {
        try {
            scheduler = event.getClient().getMusicManager().addScheduler(event.getVoiceChannel(), false);
        } catch (InsufficientPermissionException ignored) {
            if (slash) hook.editOriginal("**Cannot join VC**").queue();
            else message.reply("**Cannot join VC**").mentionRepliedUser(true).queue();
            return scheduler;
        }
        return null;
    }

    protected void send() {
        String content = "**Queue cleared**";
        if (slash) hook.editOriginal(content).queue();
        else message.reply(content).queue();
    }

}
