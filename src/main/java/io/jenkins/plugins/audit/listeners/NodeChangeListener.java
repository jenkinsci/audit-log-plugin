package io.jenkins.plugins.audit.listeners;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Node;
import io.jenkins.plugins.audit.event.CreateNode;
import io.jenkins.plugins.audit.event.UpdateNode;
import io.jenkins.plugins.audit.event.DeleteNode;
import org.apache.logging.log4j.audit.LogEventFactory;

import javax.annotation.Nonnull;
import java.util.Date;

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
        nodeCreateEvent.setTimestamp(new Date().toString());

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
        nodeUpdateEvent.setTimestamp(new Date().toString());

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
        nodeDeleteEvent.setTimestamp(new Date().toString());

        nodeDeleteEvent.logEvent();
    }

    /**
     * Returns a registered {@link NodeChangeListener} instance.
     */
    public static ExtensionList<NodeChangeListener> getInstance() {
        return ExtensionList.lookup(NodeChangeListener.class);
    }

}
