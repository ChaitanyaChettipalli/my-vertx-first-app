package io.vertx.blog.first;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class MySecondVerticleTest {


    private Vertx vertx;
    private Integer port;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        // Let's configure the verticle to listen on the 'test' port (randomly picked).
        // We create deployment options and set the _configuration_ json object:
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );

        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(MySecondVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    /**
     * Let's ensure that our application behaves correctly.
     *
     * @param context the test context
     */
    @Test
    public void testMyApplication(TestContext context) {
        // This test is asynchronous, so get an async handler to inform the test when we are done.
        final Async async = context.async();

        // We create a HTTP client and query our application. When we get the response we check it contains the 'Hello'
        // message. Then, we call the `complete` method on the async handler to declare this async (and here the test) done.
        // Notice that the assertions are made on the 'context' object and are not Junit assert. This ways it manage the
        // async aspect of the test the right way.
        vertx.createHttpClient().getNow(port, "localhost", "/", response -> {
            response.handler(body -> {
                context.assertTrue(body.toString().contains("Hello"));
                async.complete();
            });
        });
    }

    @Test
    public void testGetProfile(TestContext context) {
        final Async async = context.async();
        vertx.createHttpClient().getNow(port, "localhost", "/api/profile/get", response -> {
            response.handler(body -> {
                System.out.println("API response is : "+body.toString());
                context.assertTrue(body.toString().contains("Chaitanya"));
                async.complete();
            });
        });
    }
}
