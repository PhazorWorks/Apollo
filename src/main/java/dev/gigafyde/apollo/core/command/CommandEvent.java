package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.Client;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.commands.MessageContextCommandEvent;
import net.dv8tion.jda.api.events.interaction.commands.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.interactions.MessageCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.interactions.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.List;


@SuppressWarnings("ConstantConditions")
public class CommandEvent implements SlashCommandInteraction, MessageCommandInteraction {
    private Command command;
    private Client client;
    private Message trigger = null;
    private String argument = "";
    private CommandHandler.CommandOriginType type;
    private SlashCommandEvent slashCommandEvent;
    private MessageContextCommandEvent messageContextCommandEvent;

    public CommandEvent(Command command, Client client, CommandHandler.CommandOriginType type, Message trigger, String argument, SlashCommandEvent slashCommandEvent, MessageContextCommandEvent messageCommandEvent) {
        this.type = type;
        this.command = command;
        this.client = client;
        switch (type) {
            case REGULAR -> {
                this.argument = argument;
                this.trigger = trigger;
            }
            case SLASH -> {
                this.slashCommandEvent = slashCommandEvent;
                slashCommandEvent.deferReply().queue();
            }
            case CONTEXT -> {
                this.messageContextCommandEvent = messageCommandEvent;
            }
        }
    }

    public CommandHandler.CommandOriginType getCommandType() {
        return type;
    }

    public Client getClient() {
        return client;
    }

    public Message getMessage() {
        if (type == CommandHandler.CommandOriginType.REGULAR) return trigger;
        return null;
    }

    public User getAuthor() {
        switch (type) {
            case REGULAR -> {
                return trigger.getAuthor();
            }
            case SLASH -> {
                return slashCommandEvent.getUser();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getUser();
            }
        }
        return null;
    }

    public Member getMember() {
        switch (type) {
            case REGULAR -> {
                return trigger.getMember();
            }
            case SLASH -> {
                return slashCommandEvent.getMember();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getMember();
            }
        }
        return null;
    }

    public SelfUser getSelfUser() {
        switch (type) {
            case REGULAR -> {
                return trigger.getJDA().getSelfUser();
            }
            case SLASH -> {
                return slashCommandEvent.getJDA().getSelfUser();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getJDA().getSelfUser();
            }
        }
        return null;
    }

    public Member getSelfMember() {
        switch (type) {
            case REGULAR -> {
                return trigger.getGuild().getSelfMember();
            }
            case SLASH -> {
                return slashCommandEvent.getGuild().getSelfMember();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getGuild().getSelfMember();
            }
        }
        return null;
    }

    public String getArgument() {
        if (type == CommandHandler.CommandOriginType.REGULAR) {
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
            case REGULAR -> {
                return trigger.getGuild();
            }
            case SLASH -> {
                return slashCommandEvent.getGuild();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getGuild();
            }
        }
        return null;
    }


    public @NotNull JDA getJDA() {
        switch (type) {
            case REGULAR -> {
                return trigger.getJDA();
            }
            case SLASH -> {
                return slashCommandEvent.getJDA();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getJDA();
            }
        }
        return null;
    }

    public @NotNull TextChannel getTextChannel() {
        switch (type) {
            case REGULAR -> {
                return trigger.getTextChannel();
            }
            case SLASH -> {
                return slashCommandEvent.getTextChannel();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getTextChannel();
            }
        }
        return null;
    }

    public @NotNull ReplyAction reply(@NotNull Message content) {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.reply(content);
            }
            case CONTEXT -> {
                return messageContextCommandEvent.reply(content);
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
    public MessageChannel getChannel() {
        switch (type) {
            case REGULAR -> {
                return trigger.getChannel();
            }
            case SLASH -> {
                return slashCommandEvent.getChannel();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getChannel();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public InteractionHook getHook() {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.getHook();
            }
            case CONTEXT -> {
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
    public ReplyAction deferReply() {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.deferReply();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.deferReply();
            }
        }
        return null;
    }


    @Override
    public long getCommandIdLong() {
        return 0;
    }

    @NotNull
    public ChannelType getChannelType() {
        switch (type) {
            case REGULAR -> {
                return trigger.getChannelType();
            }
            case SLASH -> {
                return slashCommandEvent.getChannelType();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getChannelType();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public User getUser() {
        switch (type) {
            case REGULAR -> {
                return trigger.getAuthor();
            }
            case SLASH -> {
                return slashCommandEvent.getUser();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getUser();
            }
        }
        return null;
    }

    public List<Message.Attachment> getAttachments() {
        if (type == CommandHandler.CommandOriginType.REGULAR) {
            return trigger.getAttachments();
        }
        return null;
    }

    public boolean isFromGuild() {
        switch (type) {
            case REGULAR -> {
                return trigger.isFromGuild();
            }
            case SLASH -> {
                return slashCommandEvent.isFromGuild();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.isFromGuild();
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Message getTargetMessage() {
        if (type == CommandHandler.CommandOriginType.CONTEXT) {
            return messageContextCommandEvent.getTargetMessage();
        }
        return null;
    }

    @Override
    public long getTargetIdLong() {
        if (type == CommandHandler.CommandOriginType.CONTEXT) {
            return messageContextCommandEvent.getTargetIdLong();
        }
        return 0;
    }

    @Nullable
    @Override
    public String getSubcommandName() {
        if (type == CommandHandler.CommandOriginType.SLASH) {
            return slashCommandEvent.getSubcommandName();
        }
        return null;
    }

    @Nullable
    @Override
    public String getSubcommandGroup() {
        if (type == CommandHandler.CommandOriginType.SLASH) {
            return slashCommandEvent.getSubcommandGroup();
        }
        return null;
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
            case REGULAR -> {
                return trigger.getIdLong();
            }
            case SLASH -> {
                return slashCommandEvent.getIdLong();
            }
            case CONTEXT -> {
                return messageContextCommandEvent.getIdLong();
            }
        }
        return 0;
    }

    public void sendError(String error) {
        switch (type) {
            case REGULAR -> trigger.reply(error).mentionRepliedUser(true).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(error).queue();
        }
    }

    public void send(String content) {
        switch (type) {
            case REGULAR -> trigger.reply(content).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(content).queue();
        }
    }
    public void sendFile(InputStream inputStream, String name) {
        switch (type) {
            case REGULAR -> trigger.reply(inputStream, name).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(inputStream, name).queue();
        }
    }

    public void sendEmbed(EmbedBuilder embed) {
        switch (type) {
            case REGULAR -> trigger.replyEmbeds(embed.build()).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginalEmbeds(embed.build()).queue();
        }
    }
}
