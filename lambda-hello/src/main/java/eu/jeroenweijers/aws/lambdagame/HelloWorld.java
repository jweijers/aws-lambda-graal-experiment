package eu.jeroenweijers.aws.lambdagame;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class HelloWorld implements RequestHandler<Map<String, String>, String> {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();


    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        final LambdaLogger logger = context.getLogger();
        logger.log("Hello from lambda!");
        logger.log("Event: " + gson.toJson(event));
        return "Hello world!";
    }
}
