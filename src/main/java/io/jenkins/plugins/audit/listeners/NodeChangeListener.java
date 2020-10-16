package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Node;
import hudson.model.User;
import io.jenkins.plugins.audit.event.CreateNode;
import io.jenkins.plugins.audit.event.DeleteNode;
import io.jenkins.plugins.audit.event.UpdateNode;
import org.apache.logging.log4j.audit.LogEventFactory;

import javax.annotation.Nonnull;

import static io.jenkins.plugins.audit.helpers.DateTimeHelper.currentDateTimeISO;

/**
 * Listener notified when a node is created, updated or deleted.
 */
@Extension
public class NodeChangeListener extends jenkins.model.NodeListener {
    /**
     * Fired when a node is created, event logged via Log4j-audit.
     *
     * @param node Node being created
     */
    @Override
    protected void onCreated(@Nonnull Node node) {
        CreateNode nodeCreateEvent = LogEventFactory.getEvent(CreateNode.class);

        nodeCreateEvent.setNodeName(node.getNodeName());
        nodeCreateEvent.setTimestamp(currentDateTimeISO());
        User user = User.current();
        if (user != null) {
            nodeCreateEvent.setUserId(user.getId());
        }

        nodeCreateEvent.logEvent();
    }

    /**
     * Fired when a node is updated, event logged via Log4j-audit.
     *
     * @param oldOne The old node being modified
     * @param newOne The after after modification
     */
    @Override
    protected void onUpdated(@Nonnull Node oldOne, @Nonnull Node newOne) {
        UpdateNode nodeUpdateEvent = LogEventFactory.getEvent(UpdateNode.class);

        nodeUpdateEvent.setNodeName(newOne.getNodeName());
        nodeUpdateEvent.setOldNodeName(oldOne.getNodeName());
        nodeUpdateEvent.setTimestamp(currentDateTimeISO());
        User user = User.current();
        if (user != null) {
            nodeUpdateEvent.setUserId(user.getId());
        }

        nodeUpdateEvent.logEvent();
    }

    /**
     * Fired when a node is deleted, event logged via Log4j-audit.
     *
     * @param node Node being deleted
     */
    @Override
    protected void onDeleted(@Nonnull Node node) {
        DeleteNode nodeDeleteEvent = LogEventFactory.getEvent(DeleteNode.class);

        nodeDeleteEvent.setNodeName(node.getNodeName());
        nodeDeleteEvent.setTimestamp(currentDateTimeISO());
        User user = User.current();
        if (user != null) {
            nodeDeleteEvent.setUserId(user.getId());
        }

        nodeDeleteEvent.logEvent();
    }

    /**
     * Returns a registered {@link NodeChangeListener} instance.
     */
    public static ExtensionList<NodeChangeListener> getInstance() {
        return ExtensionList.lookup(NodeChangeListener.class);
    }

}
