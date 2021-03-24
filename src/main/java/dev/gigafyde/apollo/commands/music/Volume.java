package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.MusicManager;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import net.dv8tion.jda.api.entities.TextChannel;

public class Volume extends Command {
    public Volume() {
        this.name = "volume";
        this.triggers = new String[]{"volume", "vol"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        TextChannel channel = event.getTextChannel();
        MusicManager musicManager = event.getClient().getMusicManager();
        if (event.getArgument().isEmpty()) {
            int vol = musicManager.getScheduler(event.getGuild()).getPlayer().getVolume();
            channel.sendMessage("\uD83D\uDD0A **Current volume is: " + vol + "%**").queue();
        } else {
            try {
                int volume = Integer.parseInt(event.getArgument());
                musicManager.getScheduler(event.getGuild()).getPlayer().setVolume(volume);
                channel.sendMessage("\uD83D\uDD0A **Volume set to: " + musicManager.getScheduler(event.getGuild()).getPlayer().getVolume() + "%**").queue();
            } catch (NumberFormatException ignored) {
                channel.sendMessage("\u274C **Invalid number**").queue();
            }
        }
    }
}
