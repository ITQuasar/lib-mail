package com.itquasar.multiverse.mail.part;

import java.util.Collections;

/**
 *
 * @author Guilherme I F L Weizenmann <guilherme at itquasar.com>
 * @param <T>
 */
public class Inline<T> extends GenericPart<T> {

    public static final Disposition INLINE_DISPOSITION = Disposition.INLINE;

    public Inline(Part<T> part) {
        this(part.getContentId().getId(), part.getName(), part.getMimeType().getMimeType(), part.getContent());
    }

    public Inline(String contentId, String name, String mimeType, T content) {
        super(contentId, INLINE_DISPOSITION, name, mimeType, content, Collections.emptyList());
    }

    public Inline(String name, String mimeType, T content) {
        super(name, INLINE_DISPOSITION, mimeType, content);
    }

}
