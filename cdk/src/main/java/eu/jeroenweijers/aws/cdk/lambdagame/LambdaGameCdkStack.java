package eu.jeroenweijers.aws.cdk.lambdagame;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

public class LambdaGameCdkStack extends Stack {
    public LambdaGameCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public LambdaGameCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        new HelloWorldService(this, "HelloWorld");
    }
}
