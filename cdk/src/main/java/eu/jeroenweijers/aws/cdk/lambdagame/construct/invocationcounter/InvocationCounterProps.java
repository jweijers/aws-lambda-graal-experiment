package eu.jeroenweijers.aws.cdk.lambdagame.construct.invocationcounter;

import software.amazon.awscdk.services.lambda.IFunction;

public interface InvocationCounterProps {

    public static Builder builder(){
        return new Builder();
    }

    IFunction getDownstream();

    public static class Builder {
        private IFunction downstream;

        public Builder downstream(final IFunction downstream) {
            this.downstream = downstream;
            return this;
        }

        public InvocationCounterProps build(){
            if(this.downstream == null) {
                throw new NullPointerException("The downstream property is required!");
            }

            return new InvocationCounterProps() {
                @Override
                public IFunction getDownstream() {
                    return downstream;
                }
            };
        }
    }
}
