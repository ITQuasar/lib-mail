package com.itquasar.multiverse.lib.mail;

import static com.itquasar.multiverse.lib.mail.EmailSessionBuilder.MailProperties.*;
import com.itquasar.multiverse.lib.mail.util.FunctionUtils;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Session;

/**
 *
 * @author Guilherme I F L Weizenmann <guilherme at itquasar.com>
 */
public class EmailSessionBuilder {

    enum Secure {
        SSL, STARTTLS;
    }

    public static enum Protocol implements EmailProtocol {
        SMTP(Type.TRANSPORT),
        IMAP(Type.STORE),
        IMAPS(Type.STORE),
        POP3S(Type.STORE),
        POP3(Type.STORE);

        private final Type type;

        private Protocol(Type type) {
            this.type = type;
        }

        @Override
        public String javamailName() {
            return name().toLowerCase();
        }

        @Override
        public Type getType() {
            return type;
        }

        public EmailProtocol asEmailProtocol() {
            return EmailProtocol.class.cast(this);
        }

    }

    public static enum MailProperties {
        DEBUG("mail.debug"),
        HOST("mail.${protocol}.host"),
        PORT("mail.${protocol}.port"),
        SSL("mail.${protocol}.ssl.enable"),
        SSL_TRUST("mail.${protocol}.ssl.trust"),
        STARTTLS("mail.${protocol}.starttls.enable"),
        USER("mail.${protocol}.user");

        private final String template;

        private MailProperties(String template) {
            this.template = template;
        }

        public String getTemplate() {
            return template;
        }

        public String apply(String value, EmailProtocol protocol) {
            String result;
            if (protocol != null) {
                result = template.replace("${protocol}", protocol.javamailName());
            } else {
                result = template.replace(".${protocol}", "");
            }
            return result;
        }

    }

    private final Properties sessionProperties = new Properties();

    private final EmailProtocol receiveProtocol;
    private final EmailProtocol sendProtocol;
    private Authenticator authenticator = null;

    ////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ////////////////////////////////////////////////////////////////////////////
    /**
     * EmailSessionBuilder using given protocol to receive email and SMTP to
     * send.
     *
     * @param receiveProtocol The Store protocol (POP3, IMAP).
     */
    public EmailSessionBuilder(Protocol receiveProtocol) {
        this(receiveProtocol.asEmailProtocol());
    }

    /**
     * EmailSessionBuilder using given protocol to receive email and SMTP to
     * send.
     *
     * @param receiveProtocol The Store protocol (POP3, IMAP).
     */
    public EmailSessionBuilder(EmailProtocol receiveProtocol) {
        this(receiveProtocol, Protocol.SMTP);
    }

    /**
     * EmailSessionBuilder using given protocols.
     *
     * @param receiveProtocol The Store protocol (POP3, IMAP).
     * @param sendProtocol The Transport protocol (SMTP).
     */
    public EmailSessionBuilder(Protocol receiveProtocol, Protocol sendProtocol) {
        this(receiveProtocol.asEmailProtocol(), sendProtocol.asEmailProtocol());
    }

    /**
     * EmailSessionBuilder using given protocols.
     *
     * @param receiveProtocol The Store protocol (POP3, IMAP).
     * @param sendProtocol The Transport protocol (SMTP).
     */
    public EmailSessionBuilder(EmailProtocol receiveProtocol, EmailProtocol sendProtocol) {
        FunctionUtils.throwExceptionOnNullArgument(receiveProtocol, "receiveProtocol");
        FunctionUtils.throwExceptionOnNullArgument(sendProtocol, "sendProtocol");
        this.receiveProtocol = receiveProtocol;
        this.sendProtocol = sendProtocol;
        sessionProperties.put("mail.store.protocol", receiveProtocol.javamailName());
        sessionProperties.put("mail.transport.protocol", sendProtocol.javamailName());
    }

    public Session build() {
        return Session.getInstance(sessionProperties, authenticator);
    }

    /**
     * Sets the key/value pair in the mail session.
     *
     * @param key Session property to be set (see JavaMail API javadocs,
     * specially packages for SMTP,IMAP and POP3).
     * @param value Value of the property.
     * @return The same instance on which this function was called
     * ({@code  this}).
     * @throws NullPointerException When key is null.
     */
    public EmailSessionBuilder set(String key, String value) {
        FunctionUtils.throwExceptionOnNullArgument(key, "key");
        sessionProperties.put(key, value);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////
    // HELPER
    ////////////////////////////////////////////////////////////////////////////
    private EmailSessionBuilder setSessionParemeter(MailProperties template, boolean value) {
        return setSessionParemeter(template, Boolean.toString(value), null);
    }

    private EmailSessionBuilder setSessionParemeter(MailProperties template, boolean value, EmailProtocol protocol) {
        return setSessionParemeter(template, Boolean.toString(value), protocol);
    }

    private EmailSessionBuilder setSessionParemeter(MailProperties template, String value) {
        return setSessionParemeter(template, value, null);
    }

    private EmailSessionBuilder setSessionParemeter(MailProperties template, String value, EmailProtocol protocol) {
        sessionProperties.put(template.apply(value, protocol), value);
        return this;
    }
    ////////////////////////////////////////////////////////////////////////////
    // DEBUG
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Enables JavaMail API debug mode.
     *
     * @return The same instance on which this function was called
     * ({@code  this}).
     */
    public EmailSessionBuilder debug() {
        return debug(true);
    }

    /**
     * Set JavaMail API debug mode.
     *
     * @param debug
     * @return The same instance on which this function was called
     * ({@code  this}).
     */
    public EmailSessionBuilder debug(boolean debug) {
        return setSessionParemeter(DEBUG, debug);
    }

    ////////////////////////////////////////////////////////////////////////////
    // HOST
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Sets the server host to send and receive e-mails. Same for both
     * protocols.
     *
     * @param host
     * @return The same instance on which this function was called
     * ({@code  this}).
     */
    public EmailSessionBuilder server(String host) {
        return setSessionParemeter(HOST, host);
    }

    /**
     * Sets the server host to send and receive e-mails separately.
     *
     * @param receiveServer Host name to receive e-mails.
     * @param sendServer Host name to send e-mails.
     * @return The same instance on which this function was called
     * ({@code  this}).
     */
    public EmailSessionBuilder server(String receiveServer, String sendServer) {
        receiveServer(receiveServer);
        sendServer(sendServer);
        return this;
    }

    /**
     * Sets the receive server only.
     *
     * @param receiveServer Host name to receive e-mails.
     * @return The same instance on which this function was called
     * ({@code  this}).
     */
    public EmailSessionBuilder receiveServer(String receiveServer) {
        return setSessionParemeter(HOST, receiveServer);
    }

    /**
     * Sets the send server only.
     *
     * @param sendServer Host name to send e-mails.
     * @return The same instance on which this function was called
     * ({@code  this}).
     */
    public EmailSessionBuilder sendServer(String sendServer) {
        return setSessionParemeter(HOST, sendServer);
    }

    ////////////////////////////////////////////////////////////////////////////
    // SSL/STARTTLS
    ////////////////////////////////////////////////////////////////////////////
    public EmailSessionBuilder sslOn() {
        sslOnReceive();
        sslOnSend();
        return this;
    }

    public EmailSessionBuilder sslOnReceive() {
        return setSessionParemeter(SSL, true, receiveProtocol);
    }

    public EmailSessionBuilder sslOnSend() {
        return setSessionParemeter(SSL, true, sendProtocol);
    }

    public EmailSessionBuilder sslTrustAllHosts() {
        return sslTrustedHosts("*");
    }

    public EmailSessionBuilder sslTrustedHosts(String... hosts) {
        FunctionUtils.throwExceptionOnNullArgument(hosts, "hosts");
        StringBuilder buffy = new StringBuilder();
        for (String host : hosts) {
            buffy.append(host).append(" ");
        }
        String trustedHosts = buffy.toString().trim();
        setSessionParemeter(SSL_TRUST, trustedHosts, receiveProtocol);
        setSessionParemeter(SSL_TRUST, trustedHosts, sendProtocol);
        return this;
    }

    public EmailSessionBuilder startTlsOn() {
        startTlsOnReceive();
        startTlsOnSend();
        return this;
    }

    public EmailSessionBuilder startTlsOnReceive() {
        return setSessionParemeter(STARTTLS, true, receiveProtocol);
    }

    public EmailSessionBuilder startTlsOnSend() {
        return setSessionParemeter(STARTTLS, true, sendProtocol);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Authentication
    ////////////////////////////////////////////////////////////////////////////
    public EmailSessionBuilder authentication(String username, String password) {
        authentication(new Credentials(username, password));
        return this;
    }

    public EmailSessionBuilder authentication(Credentials credentials) {
        authentication(credentials.asAuthenticator());
        return this;
    }

    public EmailSessionBuilder authentication(Authenticator credentials) {
        authenticator = credentials;
        return this;
    }

}
