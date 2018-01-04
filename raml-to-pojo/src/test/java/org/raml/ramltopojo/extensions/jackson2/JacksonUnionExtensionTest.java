package org.raml.ramltopojo.extensions.jackson2;

/**
 * Created. There, you have it.
 */
public class JacksonUnionExtensionTest {

/*
    @Mock
    private CurrentBuild build;

    @Mock
    private V10GType declaration;

    @Mock
    private UnionTypeDeclaration typeDeclaration;

    @Mock
    private TypeDeclaration unionOfType;

    @Before
    public void mockito() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void makeIt() throws Exception {

        when(declaration.name()).thenReturn("Foo");
        when(declaration.implementation()).thenReturn(typeDeclaration);
        when(build.getModelPackage()).thenReturn("model");

        when(typeDeclaration.of()).thenReturn(Arrays.asList(unionOfType));
        when(typeDeclaration.name()).thenReturn("Foo");
        when(unionOfType.name()).thenReturn("UnionOf");

        UnionDeserializationGenerator generator = new UnionDeserializationGenerator(build, declaration,
                ClassName.get("foo", "CooGenerator")) {

            @Override
            protected UnionTypeDeclaration getUnionTypeDeclaration() {
                return typeDeclaration;
            }
        };

        generator.output(new CodeContainer<TypeSpec.Builder>() {

            @Override
            public void into(TypeSpec.Builder g) throws IOException {

                TypeName looksLikeType = ParameterizedTypeName.get(Map.class, String.class, Object.class);
                TypeName jsonParser = ClassName.get(JsonParser.class);
                TypeName context = ClassName.get(DeserializationContext.class);

                assertThat(g.build(), TypeSpecMatchers.name(is(equalTo("CooGenerator"))));
                assertThat(g.build(),
                        TypeSpecMatchers.methods(containsInAnyOrder(
                                allOf(
                                        MethodSpecMatchers
                                                .methodName(is(equalTo("looksLikeUnionOf"))),
                                        MethodSpecMatchers.parameters(
                                                contains(
                                                        ParameterSpecMatchers.type(is(equalTo(looksLikeType)))
                                                )
                                        )
                                ),
                                allOf(
                                        MethodSpecMatchers.methodName(is(equalTo("deserialize"))),
                                        MethodSpecMatchers.parameters(
                                                contains(
                                                        ParameterSpecMatchers
                                                                .type(is(equalTo(jsonParser))),
                                                        ParameterSpecMatchers
                                                                .type(is(equalTo(context)))
                                                )
                                        ),
                                        MethodSpecMatchers
                                                .codeContent(containsString("looksLikeUnionOf")) // approx
                                ),
                                allOf(
                                        MethodSpecMatchers.methodName(is(equalTo("<init>")))
                                )


                        )));

            }
        });
    }
*/

}