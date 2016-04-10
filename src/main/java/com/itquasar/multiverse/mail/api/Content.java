package com.itquasar.multiverse.mail.api;

import com.itquasar.multiverse.mail.part.Part;
import java.util.List;

/**
 *
 * @author Guilherme I F L Weizenmann <guilherme at itquasar.com>
 */
public interface Content {

    List<? extends Part> getAttachments();

    /**
     *
     * @return The content from the first text/html part
     */
    String getHtmlContent();

    List<? extends Part> getHtmlImages();

    /**
     *
     * @return The first text/html part
     */
    Part<String> getHtmlPart();

    /**
     *
     * @return The content from the first text/plain part
     */
    String getTextContent();

    /**
     *
     * @return The first text/plain part
     */
    Part<String> getTextPart();

    boolean hasTextHtml();

    boolean hasImages();

    boolean hasTextPlain();

}
