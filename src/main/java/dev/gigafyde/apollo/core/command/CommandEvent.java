package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.Client;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;


public class CommandEvent implements SlashCommandInteraction, MessageContextInteraction {
    private final Command command;
    private final Client client;
    private Message trigger = null;
    private String argument = "";
    private CommandInteraction commandInteraction = null;

    public CommandEvent(Command command, Client client, Message trigger, String argument, CommandInteraction commandInteraction) {
        this.command = command;
        this.client = client;
        this.argument = argument;
        this.trigger = trigger;
        if (commandInteraction != null) {
            this.commandInteraction = commandInteraction;
        }
    }

    public CommandInteraction getCommandInteraction() {
        return commandInteraction;
    }

    public Client getClient() {
        return client;
    }

    public Message getMessage() {
        return trigger;
    }

    public User getAuthor() {
        if (commandInteraction != null) return commandInteraction.getUser();
        return trigger.getAuthor();
    }

    public Member getMember() {
        if (commandInteraction != null)
            return commandInteraction.getMember();
        return trigger.getMember();
    }

    public SelfUser getSelfUser() {
        if (commandInteraction != null)
            return commandInteraction.getJDA().getSelfUser();
        return trigger.getJDA().getSelfUser();
    }

    public Member getSelfMember() {
        if (commandInteraction != null) commandInteraction.getGuild().getSelfMember();
        return trigger.getGuild().getSelfMember();
    }

    public String getArgument() {
        return argument;
    }

    @Override
    public int getTypeRaw() {
        return commandInteraction.getTypeRaw();
    }

    @NotNull
    @Override
    public String getToken() {
        return null;
    }

    public Guild getGuild() {
        if (commandInteraction != null)
            return commandInteraction.getGuild();
        return trigger.getGuild();
    }


    @NotNull
    public JDA getJDA() {
        if (commandInteraction != null)
            return commandInteraction.getJDA();
        return trigger.getJDA();
    }

    public @NotNull ReplyCallbackAction reply(@NotNull Message content) {
        if (commandInteraction != null)
            return commandInteraction.reply(MessageCreateData.fromMessage(content));
        return null;
    }

    @Override
    public net.dv8tion.jda.api.interactions.commands.Command.Type getCommandType() {
        if (commandInteraction == null) return net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE;
        return commandInteraction.getCommandType();
    }

    @NotNull
    @Override
    public String getName() {
        return command.name;
    }

    @NotNull
    @Override
    public MessageChannelUnion getChannel() {
        if (commandInteraction != null)
            return (MessageChannelUnion) commandInteraction.getChannel();
        return trigger.getChannel();
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
        if (commandInteraction != null)
            return commandInteraction.getHook();
        return null;
    }

    @Override
    public boolean isAcknowledged() {
        return false;
    }

    @NotNull
    @Override
    public ReplyCallbackAction deferReply() {
        if (commandInteraction != null)
                return commandInteraction.deferReply();
        return null;
    }


    @Override
    public long getCommandIdLong() {
        return commandInteraction.getCommandIdLong();
    }

    @Override
    public boolean isGuildCommand() {
        return commandInteraction.isGuildCommand();
    }

    @NotNull
    public ChannelType getChannelType() {
        if (commandInteraction != null)
            return commandInteraction.getChannelType();

        return trigger.getChannelType();
    }

    @NotNull
    @Override
    public User getUser() {
        if (commandInteraction != null)
            return commandInteraction.getUser();
        return trigger.getAuthor();
    }

    public List<Message.Attachment> getAttachments() {
        return trigger.getAttachments();
    }

    public boolean isFromGuild() {
        if (commandInteraction != null)
            return commandInteraction.isFromGuild();
        return trigger.isFromGuild();
    }

    @Nullable
    @Override
    public String getSubcommandName() {
        return commandInteraction.getSubcommandName();
    }

    @Nullable
    @Override
    public String getSubcommandGroup() {
        return commandInteraction.getSubcommandGroup();
    }

    @NotNull
    @Override
    public String getFullCommandName() {
        return commandInteraction.getFullCommandName();
    }

    @Override
    public @NotNull List<OptionMapping> getOptions() {
        return commandInteraction.getOptions();
    }

    public OptionMapping getOption(@Nonnull String name) {
        return commandInteraction.getOption(name);
    }


    @Override
    public long getIdLong() {
        return commandInteraction.getIdLong();
    }

    public void sendError(String error) {
        if (commandInteraction != null)
            commandInteraction.reply(error).queue();
        else {
            trigger.reply(error).mentionRepliedUser(true).queue();
        }
    }

    public void sendMessage(String content) {
        if (commandInteraction != null)
            commandInteraction.getHook().editOriginal(content).queue();
        else {
            trigger.reply(content).mentionRepliedUser(false).queue();
        }
    }

    public void sendFile(InputStream inputStream, String name) {
        if (commandInteraction != null)
            commandInteraction.getHook().editOriginal(MessageEditData.fromFiles(FileUpload.fromData(inputStream, name))).queue();
        else {
            trigger.replyFiles(FileUpload.fromData(inputStream, name)).mentionRepliedUser(false).queue();
        }
    }

    public void sendEmbed(EmbedBuilder embed) {
        if (commandInteraction != null)
            commandInteraction.getHook().editOriginalEmbeds(embed.build()).queue();
        else {
            trigger.replyEmbeds(embed.build()).mentionRepliedUser(false).queue();
        }
    }

    @NotNull
    @Override
    public ModalCallbackAction replyModal(@NotNull Modal modal) {
        return null;
    }

    @NotNull
    @Override
    public Message getTarget() {
        return commandInteraction.getHook().retrieveOriginal().complete();
    }
}
