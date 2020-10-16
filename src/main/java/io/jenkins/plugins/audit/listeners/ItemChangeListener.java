package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.User;
import io.jenkins.plugins.audit.event.CopyItem;
import io.jenkins.plugins.audit.event.CreateItem;
import io.jenkins.plugins.audit.event.DeleteItem;
import io.jenkins.plugins.audit.event.UpdateItem;
import org.apache.logging.log4j.audit.LogEventFactory;

import javax.annotation.Nonnull;

import static io.jenkins.plugins.audit.helpers.DateTimeHelper.currentDateTimeISO;

/**
 * Listener notified of any CRUD operations on items.
 */
@Extension
public class ItemChangeListener extends hudson.model.listeners.ItemListener {

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
        itemCreateEvent.setTimestamp(currentDateTimeISO());
        User user = User.current();
        if (user != null) {
            itemCreateEvent.setUserId(user.getId());
        }

        itemCreateEvent.logEvent();

    }

    /**
     * Fired when a new job is created by copying from an existing job
     *
     * @param src  is the source item that the new one was copied from.
     * @param item is the newly created item.
     */
    @Override
    public void onCopied(Item src, Item item) {
        CopyItem itemCopyEvent = LogEventFactory.getEvent(CopyItem.class);

        itemCopyEvent.setItemName(item.getName());
        itemCopyEvent.setItemUri(item.getUrl());
        itemCopyEvent.setSourceItemUri(src.getUrl());
        itemCopyEvent.setTimestamp(currentDateTimeISO());
        User user = User.current();
        if (user != null) {
            itemCopyEvent.setUserId(user.getId());
        }

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
        itemDeleteEvent.setTimestamp(currentDateTimeISO());
        User user = User.current();
        if (user != null) {
            itemDeleteEvent.setUserId(user.getId());
        }

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
        itemUpdateEvent.setTimestamp(currentDateTimeISO());
        User user = User.current();
        if (user != null) {
            itemUpdateEvent.setUserId(user.getId());
        }

        itemUpdateEvent.logEvent();
    }

    /**
     * Returns a registered {@link ItemChangeListener} instance.
     */
    public static ExtensionList<ItemChangeListener> getInstance() {
        return ExtensionList.lookup(ItemChangeListener.class);
    }

}
