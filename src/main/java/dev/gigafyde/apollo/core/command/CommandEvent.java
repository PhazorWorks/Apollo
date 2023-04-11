package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.*;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import javax.annotation.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.*;
import net.dv8tion.jda.api.interactions.*;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.context.*;
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.*;
import net.dv8tion.jda.api.utils.*;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;


public class CommandEvent implements SlashCommandInteraction, MessageContextInteraction {
    private final Command command;
    private final Client client;
    private final net.dv8tion.jda.api.interactions.commands.Command.Type type;
    private Message trigger = null;
    private String argument = "";
    private SlashCommandInteraction slashCommandEvent;
    private MessageContextInteraction messageContextCommandEvent;

    public CommandEvent(Command command, Client client, net.dv8tion.jda.api.interactions.commands.Command.Type type, Message trigger, String argument, SlashCommandInteraction slashCommandEvent, MessageContextInteraction messageCommandEvent) {
        this.type = type;
        this.command = command;
        this.client = client;
        switch (type) {
            case MESSAGE -> {
                this.argument = argument;
                this.trigger = trigger;
            }
            case SLASH -> {
                this.slashCommandEvent = slashCommandEvent;
                slashCommandEvent.deferReply().queue();
            }
            case USER -> this.messageContextCommandEvent = messageCommandEvent;
        }
    }

    public net.dv8tion.jda.api.interactions.commands.Command.Type getCommandType() {
        return type;
    }

    public Client getClient() {
        return client;
    }

    public Message getMessage() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE) return trigger;
        return null;
    }

    public User getAuthor() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getAuthor();
            }
            case SLASH -> {
                return slashCommandEvent.getUser();
            }
            case USER -> {
                return messageContextCommandEvent.getUser();
            }
        }
        return null;
    }

    public Member getMember() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getMember();
            }
            case SLASH -> {
                return slashCommandEvent.getMember();
            }
            case USER -> {
                return messageContextCommandEvent.getMember();
            }
        }
        return null;
    }

    public SelfUser getSelfUser() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getJDA().getSelfUser();
            }
            case SLASH -> {
                return slashCommandEvent.getJDA().getSelfUser();
            }
            case USER -> {
                return messageContextCommandEvent.getJDA().getSelfUser();
            }
        }
        return null;
    }

    public Member getSelfMember() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getGuild().getSelfMember();
            }
            case SLASH -> {
                return slashCommandEvent.getGuild().getSelfMember();
            }
            case USER -> {
                return messageContextCommandEvent.getGuild().getSelfMember();
            }
        }
        return null;
    }

    public String getArgument() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE) {
            return argument;
        }
        return "";
    }

    @Override
    public int getTypeRaw() {
        return 0;
    }

    @NotNull
    @Override
    public String getToken() {
        return null;
    }

    public Guild getGuild() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getGuild();
            }
            case SLASH -> {
                return slashCommandEvent.getGuild();
            }
            case USER -> {
                return messageContextCommandEvent.getGuild();
            }
        }
        return null;
    }


    public @NotNull JDA getJDA() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getJDA();
            }
            case SLASH -> {
                return slashCommandEvent.getJDA();
            }
            case USER -> {
                return messageContextCommandEvent.getJDA();
            }
        }
        return null;
    }

//    public @NotNull TextChannel getTextChannel() {
//        switch (type) {
//            case MESSAGE -> {
//                return trigger.getChannel();
//            }
//            case SLASH -> {
//                return slashCommandEvent.getTextChannel();
//            }
//            case USER -> {
//                return messageContextCommandEvent.getTextChannel();
//            }
//        }
//        return null;
//    }

    public @NotNull ReplyCallbackAction reply(@NotNull Message content) {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.reply(MessageCreateData.fromMessage(content));
            }
            case USER -> {
                return messageContextCommandEvent.reply(MessageCreateData.fromMessage(content));
            }
        }
        return null;
    }


    @NotNull
    @Override
    public String getName() {
        return command.name;
    }

    @NotNull
    @Override
    public MessageChannelUnion getChannel() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getChannel();
            }
            case SLASH -> {
                return slashCommandEvent.getChannel();
            }
            case USER -> {
                return messageContextCommandEvent.getChannel();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public GuildMessageChannelUnion getGuildChannel() {
        return SlashCommandInteraction.super.getGuildChannel();
    }

    @NotNull
    @Override
    public DiscordLocale getUserLocale() {
        return null;
    }

    @NotNull
    @Override
    public InteractionHook getHook() {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.getHook();
            }
            case USER -> {
                return messageContextCommandEvent.getHook();
            }
        }
        return null;
    }

    @Override
    public boolean isAcknowledged() {
        return false;
    }

    @NotNull
    @Override
    public ReplyCallbackAction deferReply() {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.deferReply();
            }
            case USER -> {
                return messageContextCommandEvent.deferReply();
            }
        }
        return null;
    }


    @Override
    public long getCommandIdLong() {
        return 0;
    }

    @Override
    public boolean isGuildCommand() {
        return false;
    }

    @NotNull
    public ChannelType getChannelType() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getChannelType();
            }
            case SLASH -> {
                return slashCommandEvent.getChannelType();
            }
            case USER -> {
                return messageContextCommandEvent.getChannelType();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public User getUser() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getAuthor();
            }
            case SLASH -> {
                return slashCommandEvent.getUser();
            }
            case USER -> {
                return messageContextCommandEvent.getUser();
            }
        }
        return null;
    }

    public List<Message.Attachment> getAttachments() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE) {
            return trigger.getAttachments();
        }
        return null;
    }

    public boolean isFromGuild() {
        switch (type) {
            case MESSAGE -> {
                return trigger.isFromGuild();
            }
            case SLASH -> {
                return slashCommandEvent.isFromGuild();
            }
            case USER -> {
                return messageContextCommandEvent.isFromGuild();
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Message getTarget() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.USER) {
            return messageContextCommandEvent.getTarget();
        }
        return null;
    }

    //@Override
    public long getTargetIdLong() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.USER) {
            return messageContextCommandEvent.getTarget().getIdLong();
        }
        return 0;
    }

    @Nullable
    @Override
    public String getSubcommandName() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) {
            return slashCommandEvent.getSubcommandName();
        }
        return null;
    }

    @Nullable
    @Override
    public String getSubcommandGroup() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) {
            return slashCommandEvent.getSubcommandGroup();
        }
        return null;
    }

    @NotNull
    @Override
    public String getFullCommandName() {
        return SlashCommandInteraction.super.getFullCommandName();
    }

    @Override
    public @NotNull List<OptionMapping> getOptions() {
        return slashCommandEvent.getOptions();
    }

    public OptionMapping getOption(@Nonnull String name) {
        return slashCommandEvent.getOption(name);
    }


    @Override
    public long getIdLong() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getIdLong();
            }
            case SLASH -> {
                return slashCommandEvent.getIdLong();
            }
            case USER -> {
                return messageContextCommandEvent.getIdLong();
            }
        }
        return 0;
    }

    public void sendError(String error) {
        switch (type) {
            case MESSAGE -> trigger.reply(error).mentionRepliedUser(true).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(error).queue();
        }
    }

    public void send(String content) {
        switch (type) {
            case MESSAGE -> trigger.reply(content).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(content).queue();
        }
    }

    public void sendFile(InputStream inputStream, String name) {
        switch (type) {
            case MESSAGE -> trigger.replyFiles(FileUpload.fromData(inputStream, name)).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(MessageEditData.fromFiles(FileUpload.fromData(inputStream, name))).queue();
        }
    }

    public void sendEmbed(EmbedBuilder embed) {
        switch (type) {
            case MESSAGE -> trigger.replyEmbeds(embed.build()).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginalEmbeds(embed.build()).queue();
        }
    }

    @NotNull
    @Override
    public ModalCallbackAction replyModal(@NotNull Modal modal) {
        return null;
    }
}
