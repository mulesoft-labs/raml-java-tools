package org.raml.ramltopojo;

import amf.client.model.domain.*;
import amf.client.validate.ValidationReport;
import com.google.common.collect.Streams;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.raml.testutils.UnitTest;
import webapi.Raml10;
import webapi.WebApiDocument;
import webapi.WebApiParser;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created. There, you have it.
 */
public class TypeFindersTest extends UnitTest {

    @Before
    public void before() throws ExecutionException, InterruptedException {

        WebApiParser.init().get();
    }

    @Test
    public void inTypes() throws Exception {

        List<DomainElement> ts = Arrays.asList(
                new NodeShape().withName("t1"),
                new NodeShape().withName("t2"),
                new NodeShape().withName("t3")
        );

        WebApiDocument api = (WebApiDocument) new WebApiDocument()
                .withDeclares(ts);

        List<Shape> it = Streams.stream(TypeFinders.inTypes().findTypes(api)).collect(Collectors.toList());

        assertThat(it.size()).isEqualTo(3);
        assertThat(it.get(0).name().value()).isEqualTo("t1");
        assertThat(it.get(1).name().value()).isEqualTo("t2");
        assertThat(it.get(2).name().value()).isEqualTo("t3");
    }


    @Test
    public void inLibraries() throws ExecutionException, InterruptedException {

        WebApiParser.init().get();

        final WebApiDocument document = (WebApiDocument) Raml10.parse(TypeFindersTest.class.getResource("typefinder-libraries.raml").toString()).get();
        final ValidationReport report = Raml10.validate(document).get();

        List<Shape> it = Streams.stream(TypeFinders.inLibraries().findTypes(document)).collect(Collectors.toList());

        assertThat(it.size()).isEqualTo(3);
        assertThat(it.get(0).name().value()).isEqualTo("t1");
        assertThat(it.get(1).name().value()).isEqualTo("t2");
        assertThat(it.get(2).name().value()).isEqualTo("t3");
    }

    @Test
    public void inResources() throws ExecutionException, InterruptedException {

        WebApiParser.init().get();

        WebApiDocument api = (WebApiDocument) new WebApiDocument()
                .withEncodes(
                        new WebApi().withEndPoints(
                                singletonList(
                                        new EndPoint()
                                                .withName("/foo")
                                                .withOperations(
                                                        singletonList(
                                                                new Operation()
                                                                        .withName("post")
                                                                        .withRequest(
                                                                                new Request()
                                                                                .withPayloads(singletonList(new Payload()
                                                                                        .withMediaType("application/json")
                                                                                        .withSchema(new NodeShape().withName("request"))
                                                                                ))
                                                                        ).withResponses(singletonList(
                                                                                new Response().withPayloads(singletonList(new Payload()
                                                                                        .withMediaType("application/json")
                                                                                        .withSchema(new NodeShape().withName("response"))
                                                                                )))
                                                                ))
                                                        )))
                );
        List<Shape> it = Streams.stream(TypeFinders.inResources().findTypes(api)).collect(Collectors.toList());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(it.size()).isEqualTo(2);
        softly.assertThat(it.get(0).name().value()).isEqualTo("response");
        softly.assertThat(it.get(1).name().value()).isEqualTo("request");
        softly.assertAll();
    }


    @Test
    public void everyWhere() throws ExecutionException, InterruptedException {

        final WebApiDocument document = (WebApiDocument) Raml10.parse(TypeFindersTest.class.getResource("typefinder-libraries.raml").toString()).get();
        List<Shape> it = Streams.stream(TypeFinders.everyWhere().findTypes(document)).collect(Collectors.toList());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(it.size()).isEqualTo(5);
        softly.assertThat(it.get(0).name().value()).isEqualTo("foo");
        softly.assertThat(it.get(1).name().value()).isEqualTo("schema");
        softly.assertThat(it.get(2).name().value()).isEqualTo("t1");
        softly.assertThat(it.get(3).name().value()).isEqualTo("t2");
        softly.assertThat(it.get(4).name().value()).isEqualTo("t3");

        softly.assertAll();
    }
}