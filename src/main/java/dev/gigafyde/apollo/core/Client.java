package dev.gigafyde.apollo.core;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.core.command.CommandHandler;
import dev.gigafyde.apollo.core.command.CommandRegistry;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction;
import org.jetbrains.annotations.NotNull;

public class Client extends ListenerAdapter {
    private final CommandRegistry registry = new CommandRegistry();
    private final CommandHandler handler;
    private final MusicManager musicManager = new MusicManager(this);
    private final JdaLavalink lavalink;

    public Client(JdaLavalink lavalink) {
        this.lavalink = lavalink;
        handler = new CommandHandler(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        handler.handle(event);
    }

    //@Override
    public void onSlashCommand(@NotNull SlashCommandInteraction event) {
        handler.handleSlashCommand(event);
    }

    public void onMessageContextCommand(@NotNull MessageContextInteraction event) {
        handler.handleMessageContextCommand(event);
    }

    public boolean isOwner(User user) {
        return Main.OWNER_ID.equals(user.getId());
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public JdaLavalink getLavalink() {
        return lavalink;
    }

    public String getPrefix() {
        return Main.BOT_PREFIX;
    }

    public CommandRegistry getCommandRegistry() {
        return registry;
    }
}
