package eu.jeroenweijers.aws.cdk.lambdagame;

import software.amazon.awscdk.core.App;

public class LambdaGameCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new LambdaGameCdkStack(app, "CdkStack");

        app.synth();
    }
}
