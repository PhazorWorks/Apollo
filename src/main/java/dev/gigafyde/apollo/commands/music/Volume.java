package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

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
        if (event.getArgument().isEmpty()) {
            int vol = (int) (event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getFilters().getVolume() * 100);
            channel.sendMessage("\uD83D\uDD0A **Current volume is: " + vol + "%**").queue();
        } else {
            try {
                float volume = (float) (Integer.parseInt(event.getArgument()) * 0.01);
                event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getFilters().setVolume(volume).commit();
                channel.sendMessage("\uD83D\uDD0A **Volume set to: " + (event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().getFilters().getVolume() * 100) + "%**").queue();
            } catch (NumberFormatException ignored) {
                channel.sendMessage("\u274C **Invalid number**").queue();
            }
        }
    }
}
