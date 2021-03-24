package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.Client;
import dev.gigafyde.apollo.utils.TextUtils;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandEvent {
    private final String argument;
    private final Client client;
    private final Message trigger;

    public CommandEvent(Client client, Message trigger, String argument) {
        this.argument = argument;
        this.client = client;
        this.trigger = trigger;
    }

    public Client getClient() {
        return client;
    }

    public Message getTrigger() {
        return trigger;
    }

    public User getAuthor() {
        return trigger.getAuthor();
    }

    public Member getMember() {
        return trigger.getMember();
    }

    public String getAuthorId() {
        return trigger.getAuthor().getId();
    }

    public Long getAuthorIdLong() {
        return trigger.getAuthor().getIdLong();
    }

    public SelfUser getSelfUser() {
        return trigger.getJDA().getSelfUser();
    }

    public Member getSelfMember() {
        Guild guild = trigger.getGuild();
        return guild.getSelfMember();
    }

    public String getAuthorTag() {
        return TextUtils.getTag(trigger.getAuthor());
    }

    public String getArgument() {
        return argument;
    }

    public String getContentRaw() {
        return trigger.getContentRaw();
    }

    public String getContentStripped() {
        return trigger.getContentStripped();
    }

    public String getContentDisplay() {
        return trigger.getContentDisplay();
    }

    public Guild getGuild() {
        return trigger.getGuild();
    }

    public String getGuildName() {
        return trigger.getGuild().getName();
    }

    public String getGuildId() {
        return trigger.getGuild().getId();
    }

    public Long getGuildIdLong() {
        return trigger.getGuild().getIdLong();
    }

    public JDA getJDA() {
        return trigger.getJDA();
    }

    public TextChannel getTextChannel() {
        return trigger.getTextChannel();
    }

    public MessageChannel getChannel() {
        return trigger.getChannel();
    }

    public String getChannelId() {
        return trigger.getChannel().getId();
    }

    public Long getChannelIdLong() {
        return trigger.getChannel().getIdLong();
    }

    public String getChannelName() {
        return trigger.getChannel().getName();
    }

    public ChannelType getChannelType() {
        return trigger.getChannelType();
    }

    public String getMessageId() {
        return trigger.getId();
    }

    public Long getMessageIdLong() {
        return trigger.getIdLong();
    }

    public MessageType getMessageType() {
        return trigger.getType();
    }

    public boolean isWebhookMessage() {
        return trigger.isWebhookMessage();
    }

    public boolean isFromBot() {
        return trigger.getAuthor().isBot();
    }

    public List<Message.Attachment> getAttachments() {
        return trigger.getAttachments();
    }

    public boolean isFromGuild() {
        return trigger.isFromType(ChannelType.TEXT);
    }

    public boolean isFromDMs() {
        return trigger.isFromType(ChannelType.PRIVATE);
    }

    public Category getCategory() {
        return trigger.getCategory();
    }

    public String getJumpUrl() {
        return trigger.getJumpUrl();
    }

}
