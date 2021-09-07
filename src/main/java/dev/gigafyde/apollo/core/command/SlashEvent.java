package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */


import dev.gigafyde.apollo.core.Client;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

public class SlashEvent {
    private final Client client;
    private final SlashCommandEvent event;

    public SlashEvent(Client client, SlashCommandEvent slashCommandEvent) {
        this.client = client;
        this.event = slashCommandEvent;
    }

    public SlashCommandEvent getSlashCommandEvent() {
        return event;
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }

    public Client getClient() {
        return client;
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public VoiceChannel getVoiceChannel() {
        return event.getVoiceChannel();
    }

    public User getUser() {
        return event.getUser();
    }

    public Member getMember() {
        return event.getMember();
    }


    // TODO add these methods after testing
    public ReplyAction reply(Message message) {
        return event.reply(message);
    }

    public ReplyAction reply(String message) {
        return event.reply(message);
    }
//    Not yet tested code, seems like it should work though.

}
