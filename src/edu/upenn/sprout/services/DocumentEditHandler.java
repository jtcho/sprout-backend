package edu.upenn.sprout.services;

import edu.upenn.sprout.api.models.EditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * A daemon that processes edit events corresponding to edits to shadow copies of documents.
 *
 * @author jtcho
 * @version 2016.11.07
 */
public class DocumentEditHandler implements Runnable {

  private static Logger LOG = LoggerFactory.getLogger(DocumentEditHandler.class);

  private DocumentDiffPatchService service;
  private boolean stopped = false;
  private Queue<EditEvent> shadowEditQueue;
  private Queue<EditEvent> masterEditQueue;

  public DocumentEditHandler(DocumentDiffPatchService service, Queue<EditEvent> shadowEditQueue, Queue<EditEvent> masterEditQueue) {
    this.service = service;
    this.shadowEditQueue = shadowEditQueue;
    this.masterEditQueue = masterEditQueue;
  }

  /**
   * Shuts down the daemon.
   */
  public void shutdown() {
    stopped = true;
  }

  @Override
  public void run() {
    LOG.info("Starting Shadow Copy Daemon.");

    while (! stopped) {
      if (! shadowEditQueue.isEmpty()) {
        EditEvent event = shadowEditQueue.poll();
        LOG.info("Processing shadow edit event: " + event);
        service.applyShadowEdit(event.getDocumentId(), event);
      }
      if (! masterEditQueue.isEmpty()) {
        EditEvent event = masterEditQueue.poll();
        LOG.info("Processing master edit event: " + event);
        service.applyMasterEdit(event.getDocumentId(), event);
      }
      Thread.yield();
    }

    LOG.info("Shutting down Shadow Copy Daemon.");
  }

}
