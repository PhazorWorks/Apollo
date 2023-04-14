package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.command.CommandHandler;
import dev.gigafyde.apollo.core.command.CommandRegistry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.NotNull;

public class Client extends ListenerAdapter {
    private final CommandRegistry registry = new CommandRegistry();
    private final CommandHandler handler;
    private final MusicManager musicManager = new MusicManager();

    public Client() {
        handler = new CommandHandler(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        handler.handle(event);
    }

    @Override
    public void onGenericCommandInteraction(@NotNull GenericCommandInteractionEvent event) {
        handler.handleCommandInteraction(event);
    }

    public boolean isOwner(User user) {
        return Main.OWNER_ID.equals(user.getId());
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }
    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return musicManager.getGuildMusicManager(guild);
    }

    public String getPrefix() {
        return Main.BOT_PREFIX;
    }

    public CommandRegistry getCommandRegistry() {
        return registry;
    }
}
