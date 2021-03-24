package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.MusicManager;
import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Clear extends Command {
    public Clear() {
        this.name = "clear";
        this.triggers = new String[]{"clear", "cls"};
    }

    public void execute(CommandEvent event) {
        MusicManager musicManager = event.getClient().getMusicManager();
        TrackScheduler scheduler = musicManager.getScheduler(event.getGuild());
        if (scheduler == null) {
            VoiceChannel vc = event.getMember().getVoiceState().getChannel();
            if (vc == null) {
                event.getTrigger().reply("**Please join a voice channel first!**").mentionRepliedUser(true).queue();
                return;
            }
            try {
                scheduler = event.getClient().getMusicManager().addScheduler(vc, false);
            } catch (InsufficientPermissionException ignored) {
                event.getTrigger().reply("**Cannot join VC**").mentionRepliedUser(true).queue();
                return;
            }
        }
        scheduler.getQueue().clear();
        event.getTrigger().reply("**Queue cleared**").queue();
    }
}
