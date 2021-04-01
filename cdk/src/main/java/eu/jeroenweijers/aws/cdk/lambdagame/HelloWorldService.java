package eu.jeroenweijers.aws.cdk.lambdagame;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.services.lambda.AssetCode;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.Bucket;

import java.util.HashMap;

public class HelloWorldService extends Construct {

    public HelloWorldService(software.constructs.@NotNull Construct scope, @NotNull String id) {
        super(scope, id);

        Bucket bucket = new Bucket(this, "softwareStore");
        //todo this artificat id should be injected or not change based on version number
        final AssetCode code = Code.fromAsset("../lambda-hello/target/hello-world-lambda.jar");
        Function helloWorldLambda = Function.Builder.create(this, "HelloWorldHandler")
                .runtime(Runtime.JAVA_11)
                .code(code)
                .handler("eu.jeroenweijers.aws.lambdagame.HelloWorld::handleRequest")
                .environment(new HashMap<String, String>() {{
                    put("BUCKET", bucket.getBucketName());
                }}).build();

        bucket.grantReadWrite(helloWorldLambda);

    }
}
