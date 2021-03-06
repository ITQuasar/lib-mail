package com.itquasar.multiverse.mail.server;

import com.itquasar.multiverse.mail.message.ImmutableParsedEmail;
import com.itquasar.multiverse.mail.message.ParsedEmail;
import com.itquasar.multiverse.mail.util.Constants;
import com.itquasar.multiverse.mail.util.ServerUtils;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Guilherme I F L Weizenmann <guilherme at itquasar.com>
 */
public class EmailFolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailFolder.class);

    private final String name;
    private final Folder folder;
    private final EmailServer emailServer;

    public EmailFolder(Store store, String name, boolean readAndWrite, final EmailServer outer) {
        this.emailServer = outer;
        try {
            this.name = name;
            this.folder = store.getFolder(name);
            folder.addConnectionListener(Constants.LoggerMailListener.CONNECTION);
            folder.addFolderListener(Constants.LoggerMailListener.FOLDER);
            folder.addMessageChangedListener(Constants.LoggerMailListener.MESSAGE_CHANGED);
            folder.addMessageCountListener(Constants.LoggerMailListener.MESSAGE_COUNT);
            int mode = readAndWrite ? Folder.READ_WRITE : Folder.READ_ONLY;
            folder.open(mode);
        } catch (MessagingException ex) {
            throw ServerUtils.logErrorAndGetNewException("Error opening folder " + name, ex, LOGGER);
        }
    }

    public void closeAndExpunge() {
        closeAndExpunge(true);
    }

    public void closeAndNotExpunge() {
        closeAndExpunge(false);
    }

    public void closeAndExpunge(boolean expunge) {
        try {
            this.folder.close(expunge);
        } catch (MessagingException ex) {
            throw ServerUtils.logErrorAndGetNewException("Error closing folder " + name, ex, LOGGER);
        }
    }

    public List<ParsedEmail> receive() {
        try {
            return filterMessgesToEmails(folder.getMessages(), null);
        } catch (MessagingException ex) {
            throw ServerUtils.logErrorAndGetNewException("Error searching messages from folder " + name, ex, LOGGER);
        }
    }

    public List<ParsedEmail> receive(SearchTerm filter) {
        try {
            return filterMessgesToEmails(folder.search(filter), null);
        } catch (MessagingException ex) {
            throw ServerUtils.logErrorAndGetNewException("Error searching messages from folder " + name, ex, LOGGER);
        }
    }

    public List<ParsedEmail> receive(Predicate<ParsedEmail> filter) {
        try {
            return filterMessgesToEmails(folder.getMessages(), filter);
        } catch (MessagingException ex) {
            throw ServerUtils.logErrorAndGetNewException("Error filtering messages from folder " + name, ex, LOGGER);
        }
    }

    private List<ParsedEmail> filterMessgesToEmails(Message[] messages, Predicate<ParsedEmail> filter) {
        Stream<ParsedEmail> emailStream = Arrays.asList(messages)
                .stream()
                .map((Message msg) -> new ImmutableParsedEmail(msg));
        if (filter != null) {
            emailStream.filter(filter);
        }
        return emailStream.collect(Collectors.toList());
    }

    public Folder getMessageFolder() {
        return folder;
    }

    public EmailServer getServer() {
        return emailServer;
    }

    @Override
    public String toString() {
        return "EmailFolder(" + name + ":" + folder + ')';
    }

}
