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
public class ShadowCopyHandler implements Runnable {

  private static Logger LOG = LoggerFactory.getLogger(ShadowCopyHandler.class);

  private DocumentDiffPatchService service;
  private boolean stopped = false;
  private Queue<EditEvent> editQueue;

  public ShadowCopyHandler(DocumentDiffPatchService service, Queue<EditEvent> editQueue) {
    this.service = service;
    this.editQueue = editQueue;
  }

  public void shutdown() {
    stopped = true;
  }

  @Override
  public void run() {
    LOG.info("Starting Shadow Copy Daemon.");

    while (! stopped) {
      if (! editQueue.isEmpty()) {
        EditEvent event = editQueue.poll();
        LOG.info("Processing edit event: " + event);
        service.applyShadowEdit(event.getDocumentId(), event);
      }
      Thread.yield();
    }

    LOG.info("Shutting down Shadow Copy Daemon.");
  }

}