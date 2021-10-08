package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
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
                if (scheduler == null)
                    sendError("**Nothing is currently playing!**");
                clear();
                send("The queue has been cleared.");
            }
            case SLASH -> {
                hook = event.getHook();
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
                if (scheduler == null)
                    scheduler = event.getClient().getMusicManager().addScheduler(event.getMember().getVoiceState().getChannel(), false);
                clear();
                send("The queue has been cleared.");
            }
        }
    }

    protected void clear() {
        scheduler.getQueue().clear();
        scheduler.skip();
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
