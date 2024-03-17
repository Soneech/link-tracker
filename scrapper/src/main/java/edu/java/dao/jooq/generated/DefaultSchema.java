/*
 * This file is generated by jOOQ.
 */

package edu.java.dao.jooq.generated;


import edu.java.dao.jooq.generated.tables.Chat;
import edu.java.dao.jooq.generated.tables.ChatLink;
import edu.java.dao.jooq.generated.tables.Link;
import java.util.Arrays;
import java.util.List;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>CHAT</code>.
     */
    public final Chat CHAT = Chat.CHAT;

    /**
     * The table <code>CHAT_LINK</code>.
     */
    public final ChatLink CHAT_LINK = ChatLink.CHAT_LINK;

    /**
     * The table <code>LINK</code>.
     */
    public final Link LINK = Link.LINK;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    @NotNull
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    @NotNull
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Chat.CHAT,
            ChatLink.CHAT_LINK,
            Link.LINK
        );
    }
}
