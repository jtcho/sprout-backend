package edu.upenn.sprout.db;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import edu.upenn.sprout.models.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author jtcho
 */
@Singleton
public class DynamoDBClient {

  private final Logger LOG = LoggerFactory.getLogger(DynamoDBClient.class);
  private static AmazonDynamoDBAsyncClient dynamoDB;

  /**
   * Instantiates the DynamoDB module.
   */
  @Inject
  public DynamoDBClient(Configuration config) {

    String accessKey = config.getString("db.aws.accessKey");
    String secretKey = config.getString("db.aws.secretKey");
    String endpoint =  config.getString("db.aws.endpoint");
    dynamoDB = new AmazonDynamoDBAsyncClient(new BasicAWSCredentials(accessKey, secretKey));
    dynamoDB.setEndpoint(endpoint);

//    LOG.info("Registered the DynamoDB client with access key " + accessKey + ", secret key " + secretKey + ", endpoint " + endpoint);
    LOG.info("Registered the DynamoDBClient.");
  }

  /**
   * Issues an asynchronous request to create a new table with a single primary key.
   *
   * @param tableName the table name
   * @param primaryKeyAttributeName the name of the primary key string
   * @return a future for the task
   */
  public CompletableFuture<CreateTableResult> createTable(String tableName, String primaryKeyAttributeName) {
    List<KeySchemaElement> keySchema = new ArrayList<>();
    keySchema.add(new KeySchemaElement()
        .withAttributeName(primaryKeyAttributeName)
        .withKeyType(KeyType.HASH)
    );

    List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
    attributeDefinitions.add(new AttributeDefinition()
      .withAttributeName(primaryKeyAttributeName)
      .withAttributeType(ScalarAttributeType.S)
    );

    CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
        .withKeySchema(keySchema)
        .withAttributeDefinitions(attributeDefinitions)
        .withProvisionedThroughput(new ProvisionedThroughput()
          .withReadCapacityUnits(1L)
          .withWriteCapacityUnits(1L));

    CompletableFuture<CreateTableResult> future = new CompletableFuture<>();
    dynamoDB.createTableAsync(createTableRequest, getAsyncHandler(future));
    return future;
  }

  /**
   *
   * @param tableName
   * @param model
   * @return
   */
  public CompletableFuture<PutItemResult> putItem(String tableName, Model model) {
    PutItemRequest putItemRequest = new PutItemRequest().withTableName(tableName)
                                                        .withItem(model.getDataAsAttributes());

    CompletableFuture<PutItemResult> future = new CompletableFuture<>();
    dynamoDB.putItemAsync(putItemRequest, getAsyncHandler(future));
    return future;
  }

  /**
   * Binds a new AWS asynchronous handler callback to a completable future.
   * When the callback is invoked (either on error or success), the future
   * is updated to reflect its status.
   *
   * @param future the future object to bind
   * @param <U> the type of the AmazonWebServiceRequest
   * @param <V> the type of the AmazonWebServiceResult
   * @return the new async handler
   */
  private static <U extends AmazonWebServiceRequest, V extends AmazonWebServiceResult> AsyncHandler<U, V> getAsyncHandler(CompletableFuture<V> future) {
    return new AsyncHandler<U, V>() {
      @Override
      public void onError(Exception exception) {
        future.completeExceptionally(exception);
      }

      @Override
      public void onSuccess(U request, V result) {
        future.complete(result);
      }
    };
  }


}
