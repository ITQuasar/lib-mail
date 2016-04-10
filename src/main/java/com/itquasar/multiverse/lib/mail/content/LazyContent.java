package com.itquasar.multiverse.lib.mail.content;

import com.itquasar.multiverse.lib.mail.Content;
import com.itquasar.multiverse.lib.mail.part.Part;
import com.itquasar.multiverse.lib.mail.util.Parser;
import java.util.List;
import javax.mail.Message;

/**
 *
 * @author Guilherme I F L Weizenmann <guilherme at itquasar.com>
 */
public class LazyContent implements Content {

    private final Message message;
    private Content content;

    public LazyContent(Message message) {
        this.message = message;
    }

    private void init() {
        if (content == null) {
            content = Parser.parseMessageContent(message);
        }
    }

    @Override
    public List<Part> getAttachments() {
        this.init();
        return content.getAttachments();
    }

    @Override
    public String getHtmlContent() {
        this.init();
        return content.getHtmlContent();
    }

    @Override
    public List<Part> getHtmlImages() {
        this.init();
        return content.getHtmlImages();
    }

    @Override
    public Part<String> getHtmlPart() {
        this.init();
        return content.getHtmlPart();
    }

    @Override
    public String getTextContent() {
        this.init();
        return content.getTextContent();
    }

    @Override
    public Part<String> getTextPart() {
        this.init();
        return content.getTextPart();
    }

    @Override
    public boolean hasTextHtml() {
        this.init();
        return content.hasTextHtml();
    }

    @Override
    public boolean hasImages() {
        this.init();
        return content.hasImages();
    }

    @Override
    public boolean hasTextPlain() {
        this.init();
        return content.hasTextPlain();
    }

}