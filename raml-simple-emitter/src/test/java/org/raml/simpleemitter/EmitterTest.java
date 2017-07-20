package org.raml.simpleemitter;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.Node;

import java.io.Writer;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class EmitterTest extends UnitTest {

    @Mock
    private Writer writer;

    @Mock
    private NodeApi api;

    @Mock
    private Node node;

    @Mock
    private Node child1;

    @Mock
    private Node child2;

    @Mock
    private HandlerList list;

    @Mock
    private YamlEmitter yamlEmitter;

    @Test
    public void emit() throws Exception {

        when(api.getNode()).thenReturn(node);
        when(node.getChildren()).thenReturn(Arrays.asList(child1, child2));
        Emitter emitter = new Emitter(list) {
            @Override
            protected YamlEmitter createEmitter(Writer w) {

                assertSame(w, writer);
                return yamlEmitter;
            }
        };
        emitter.emit(api, writer);


        verify(list).handle(eq(child1), any(YamlEmitter.class) );
        verify(list).handle(eq(child2), any(YamlEmitter.class) );
    }


    public interface NodeApi extends NodeModel, Api {

    }

}