package edu.upenn.sprout.services;

import edu.upenn.sprout.api.models.InternalEditEvent;
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
  private Queue<InternalEditEvent> shadowEditQueue;
  private Queue<InternalEditEvent> masterEditQueue;

  public DocumentEditHandler(DocumentDiffPatchService service, Queue<InternalEditEvent> shadowEditQueue,
                             Queue<InternalEditEvent> masterEditQueue) {
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
        InternalEditEvent event = shadowEditQueue.poll();
        LOG.info("Processing shadow edit event: " + event);
        service.applyShadowEdit(event.getDocumentId(), event);
      }
      if (! masterEditQueue.isEmpty()) {
        InternalEditEvent event = masterEditQueue.poll();
        LOG.info("Processing master edit event: " + event);
        service.applyMasterEdit(event.getDocumentId(), event);
      }
      Thread.yield();
    }

    LOG.info("Shutting down Shadow Copy Daemon.");
  }

}
