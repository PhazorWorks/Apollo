package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Connect extends Command {
    public Connect() {
        this.name = "connect";
        this.triggers = new String[]{"connect", "summon"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        VoiceChannel vc = event.getMember().getVoiceState().getChannel();
        if (vc == null) {
            channel.sendMessage("**Make sure to connect to a voice channel first.**").queue();
            return;
        }
        if (vc == event.getSelfMember().getVoiceState().getChannel()) {
            channel.sendMessage("**Already connected to **`" + vc.getName() + "`").queue();
            return;
        }
        try {
            event.getClient().getMusicManager().moveVoiceChannel(vc);
            channel.sendMessage("**Connected to **`" + vc.getName() + "`").queue();
        } catch (InsufficientPermissionException ignored) {
            channel.sendMessage("**Failed to connect to the desired voice channel.**").queue();
        }
    }
}
