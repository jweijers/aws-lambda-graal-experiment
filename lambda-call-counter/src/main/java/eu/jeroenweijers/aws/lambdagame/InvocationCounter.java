package eu.jeroenweijers.aws.lambdagame;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LogType;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@Named("invocationCounter")
public class InvocationCounter implements RequestHandler<Map<String, String>, String> {

    private static final String INVOCATION_TABLE_VAR = "InvocationTable";
    private static final String DOWNSTREAM_FUNCTION_NAME = "DownstreamFunction";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final LambdaClient lambdaClient = LambdaClient.builder()
            .httpClient(UrlConnectionHttpClient.builder().build()).build();

    private final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .httpClient(UrlConnectionHttpClient.builder().build()).build();

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        final LambdaLogger logger = context.getLogger();

        final String lambdaToInvoke = System.getenv(DOWNSTREAM_FUNCTION_NAME);
        logger.log("Invoking: " + lambdaToInvoke);

        incrementInvocationCount(logger, lambdaToInvoke);

        return invokeLambda(event);
    }

    private void incrementInvocationCount(LambdaLogger logger, String lambdaToInvoke) {
        HashMap<String, AttributeValue> itemKey = new HashMap<>();

        itemKey.put("Function", AttributeValue.builder().s(lambdaToInvoke).build());

        final HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":incr", AttributeValue.builder().n("1").build());
        final UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(System.getenv(INVOCATION_TABLE_VAR))
                .key(itemKey)
                .updateExpression("ADD invocations :incr")
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        final UpdateItemResponse response = dynamoDbClient.updateItem(updateItemRequest);
        logger.log(response.toString());
    }

    private String invokeLambda(Map<String, String> event) {
        SdkBytes payload = SdkBytes.fromUtf8String(gson.toJson(event));

        InvokeRequest request = InvokeRequest.builder()
                .functionName(System.getenv(DOWNSTREAM_FUNCTION_NAME))
                .payload(payload)
                .invocationType(InvocationType.REQUEST_RESPONSE)
                .logType(LogType.TAIL)
                .build();

        InvokeResponse res = lambdaClient.invoke(request);
        return res.payload().asUtf8String();
    }
}
