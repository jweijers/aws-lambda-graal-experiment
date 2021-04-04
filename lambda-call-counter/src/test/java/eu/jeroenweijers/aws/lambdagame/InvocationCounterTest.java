package eu.jeroenweijers.aws.lambdagame;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InvocationCounterTest {

    InvocationCounter sut = new InvocationCounter();

    @Test
    @Disabled
    void testInvoke(){
        assertEquals("Hello world!" , sut.handleRequest(new HashMap<>(), null));
    }

}
