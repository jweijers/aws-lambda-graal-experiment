package eu.jeroenweijers.aws.lambdagame;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LogType;

import java.util.HashMap;
import java.util.Map;

public class InvocationCounter implements RequestHandler<Map<String, String>, String> {

    private static final String INVOCATION_TABLE_VAR = "InvocationTable";
    private static final String DOWNSTREAM_FUNCTION_NAME = "DownstreamFunction";

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        new InvocationCounter().handleRequest(new HashMap<>(), null);
    }

    final LambdaClient lambdaClient = LambdaClient.builder().build();
    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        final Logger logger = LoggerFactory.getLogger(InvocationCounter.class);


        logger.info("Wrapping lambda!");
        logger.info("Context: " + gson.toJson(context));
        logger.info("Invoking: " + System.getenv(DOWNSTREAM_FUNCTION_NAME));
        logger.info("In region: " + System.getenv("AWS_REGION"));


        SdkBytes payload = SdkBytes.fromUtf8String(gson.toJson(event));

        logger.info("Pre request create");
        InvokeRequest request = InvokeRequest.builder()
                .functionName(System.getenv(DOWNSTREAM_FUNCTION_NAME))
                .payload(payload)
                .invocationType(InvocationType.REQUEST_RESPONSE)
                .logType(LogType.TAIL)
                .build();

        logger.info("Pre invoke");
        //Invoke the Lambda function
        try {
            InvokeResponse res = lambdaClient.invoke(request);
            logger.info("Post invoke");
            String value = res.payload().asUtf8String();
            return value;
        } catch (Exception e) {
            logger.error("Something went horribly wrong " + e.getMessage());
        }
        return "Error";
    }
}
