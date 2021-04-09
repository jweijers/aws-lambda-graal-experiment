package eu.jeroenweijers.aws.cdk.lambdagame.construct.invocationcounter;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.AssetCode;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.HashMap;
import java.util.Map;

public class InvocationCounter extends Construct {

    private final Function handler;

    private final Table table;

    public InvocationCounter(final Construct scope, final String id, final InvocationCounterProps props) {
        super(scope, id);

        this.table = Table.Builder.create(this, "Invocations")
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .partitionKey(Attribute.builder()
                        .name("Function")
                        .type(AttributeType.STRING)
                        .build())
                .build();

        final Map<String, String> environment = new HashMap<>();
        environment.put("DownstreamFunction", props.getDownstream().getFunctionName());
        environment.put("InvocationTable", table.getTableName());

        final AssetCode code = Code.fromAsset("../lambda-call-counter/target/function.zip");
        this.handler = Function.Builder.create(this, "InvocationCounterHandler")
                .runtime(Runtime.PROVIDED_AL2)
                .handler("io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest")
                //somehow this is very slow and consumes lots of memory...
                .timeout(Duration.seconds(5))
                .code(code)
                .environment(environment)
                .build();

        props.getDownstream().grantInvoke(handler);
        table.grantReadWriteData(handler);
    }

    public Function getHandler() {
        return handler;
    }
}
