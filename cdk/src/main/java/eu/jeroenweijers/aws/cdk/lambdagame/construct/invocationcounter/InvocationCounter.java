package eu.jeroenweijers.aws.cdk.lambdagame.construct.invocationcounter;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.lambda.AssetCode;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.HashMap;
import java.util.Map;

public class InvocationCounter extends Construct {

    private final Function handler;
    public InvocationCounter(final Construct scope, final String id, final InvocationCounterProps props) {
        super(scope, id);

        final Map<String, String> environment = new HashMap<>();
        environment.put("DownstreamFunction", props.getDownstream().getFunctionName());

        final AssetCode code = Code.fromAsset("../lambda-call-counter/target/call-counter-lambda.jar");
        this.handler = Function.Builder.create(this, "InvocationCounterHandler")
                .runtime(Runtime.JAVA_11)
                .handler("eu.jeroenweijers.aws.lambdagame.InvocationCounter::handleRequest")
                //somehow this is very slow and consumes lots of memory...
                .timeout(Duration.seconds(15))
                .memorySize(256)
                .code(code)
                .environment(environment)
                .build();

        props.getDownstream().grantInvoke(handler);
    }

    public Function getHandler() {
        return handler;
    }
}
