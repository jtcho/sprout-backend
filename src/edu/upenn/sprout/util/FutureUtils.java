package edu.upenn.sprout.util;

import play.libs.F;

import java.util.concurrent.CompletableFuture;

/**
 * @author jtcho
 * @version 2016.09.17
 */
public class FutureUtils {

  /**
   * Wraps a CompletableFuture object in a Play Framework Promise.
   *
   * Using Gradle as a build tool restricts use of Play to version 2.4.x.
   * Play does not support the returning of CompletableFuture types in
   * controllers until version 2.5.x.
   *
   * This is a temporary solution until the Gradle Play plugin is updated.
   *
   * @param future the future to wrap
   * @param <T> the enclosed type in the future
   * @return the enclosing promise
   */
  public static <T> F.Promise<T> asPromise(CompletableFuture<T> future) {
    F.RedeemablePromise<T> promise = F.RedeemablePromise.empty();
    future.whenCompleteAsync((res, err) -> {
      if (err != null) {
        promise.failure(err);
      }
      else {
        promise.success(res);
      }
    });
    return promise;
  }

}
