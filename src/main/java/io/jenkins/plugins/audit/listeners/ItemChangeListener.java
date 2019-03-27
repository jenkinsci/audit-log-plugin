package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import io.jenkins.plugins.audit.event.*;
import org.apache.logging.log4j.audit.LogEventFactory;
import static io.jenkins.plugins.audit.helpers.DateTimeHelper.formatDateISO;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Listener notified of any CRUD operations on items.
 */
@Extension
public class ItemChangeListener extends hudson.model.listeners.ItemListener{

    /**
     * Fired when a new job is created and added to Jenkins, before the initial configuration page is provided.
     *
     * @param item is a job that is created
     */
    @Override
    public void onCreated(@Nonnull Item item) {
        CreateItem itemCreateEvent = LogEventFactory.getEvent(CreateItem.class);

        itemCreateEvent.setItemName(item.getName());
        itemCreateEvent.setItemUri(item.getUrl());
        itemCreateEvent.setTimestamp(formatDateISO(Instant.now().toEpochMilli()));

        itemCreateEvent.logEvent();

    }

    /**
     * Fired when a new job is created by copying from an existing job
     *
     * @param src is the source item that the new one was copied from.
     * @param item is the newly created item.
     */
    @Override
    public void onCopied(Item src, Item item) {
        CopyItem itemCopyEvent = LogEventFactory.getEvent(CopyItem.class);

        itemCopyEvent.setItemName(item.getName());
        itemCopyEvent.setItemUri(item.getUrl());
        itemCopyEvent.setSourceItemUri(src.getUrl());
        itemCopyEvent.setTimestamp(formatDateISO(Instant.now().toEpochMilli()));

        itemCopyEvent.logEvent();

    }

    /**
     * Fired right before a job is going to be deleted
     *
     * @param item is item to be deleted.
     */
    @Override
    public void onDeleted(Item item) {
        DeleteItem itemDeleteEvent = LogEventFactory.getEvent(DeleteItem.class);

        itemDeleteEvent.setItemName(item.getName());
        itemDeleteEvent.setItemUri(item.getUrl());
        itemDeleteEvent.setTimestamp(formatDateISO(Instant.now().toEpochMilli()));

        itemDeleteEvent.logEvent();

    }

    /**
     * Fired when a job has its configuration updated.
     *
     * @param item is job being updated.
     */
    @Override
    public void onUpdated(Item item) {
        UpdateItem itemUpdateEvent = LogEventFactory.getEvent(UpdateItem.class);

        itemUpdateEvent.setItemName(item.getName());
        itemUpdateEvent.setItemUri(item.getUrl());
        itemUpdateEvent.setTimestamp(formatDateISO(Instant.now().toEpochMilli()));

        itemUpdateEvent.logEvent();
    }

    /**
     * Returns a registered {@link ItemChangeListener} instance.
     */
    public static ExtensionList<ItemChangeListener> getInstance() {
        return ExtensionList.lookup(ItemChangeListener.class);
    }

}
