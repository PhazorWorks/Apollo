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
    private CommandEvent event;

    public Clear() {
        this.name = "clear";
        this.triggers = new String[]{"clear", "cls", "clr"};
    }

    protected void execute(CommandEvent event) {

        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) {
                    scheduler = makeScheduler();
                }
                clear(scheduler);
                send("**Queue cleared**");
            }
            case SLASH -> {
                hook = event.getHook();
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null) {
                    scheduler = makeScheduler();
                }
                clear(scheduler);
                send("**Queue cleared**");
            }
        }
    }

    protected void clear(TrackScheduler scheduler) {
        scheduler.getQueue().clear();
        scheduler.skip();
    }

    protected TrackScheduler makeScheduler() {
        try {
            scheduler = event.getClient().getMusicManager().addScheduler(event.getMember().getVoiceState().getChannel(), false);
        } catch (InsufficientPermissionException ignored) {
            switch (event.getCommandType()) {
                case SLASH -> hook.editOriginal("**Cannot join VC**").queue();
                case REGULAR -> message.reply("**Cannot join VC**").mentionRepliedUser(true).queue();
            }
        }
        return null;
    }

    protected void send(String content) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(content).queue();
            case SLASH -> hook.editOriginal(content).queue();
        }
    }

}
