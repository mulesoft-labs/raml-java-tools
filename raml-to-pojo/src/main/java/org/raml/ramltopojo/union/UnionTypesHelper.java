package org.raml.ramltopojo.union;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.Utils;
import org.raml.v2.api.model.v10.datamodel.*;

import java.util.*;
import java.util.function.Function;

public class UnionTypesHelper {

    private static Map<Class<? extends TypeDeclaration>, Integer> getPriorityTypeMap() {
        return new ImmutableMap.Builder<Class<? extends TypeDeclaration>, Integer>()
            .put(NullTypeDeclaration.class, 1)
            .put(BooleanTypeDeclaration.class, 2)
            .put(IntegerTypeDeclaration.class, 3)
            .put(NumberTypeDeclaration.class, 4)
            .put(DateTypeDeclaration.class, 5)
            .put(TimeOnlyTypeDeclaration.class, 6)
            .put(DateTimeOnlyTypeDeclaration.class, 7)
            .put(DateTimeTypeDeclaration.class, 8)
            .put(StringTypeDeclaration.class, 9)
            .put(ObjectTypeDeclaration.class, 10)
            .put(ArrayTypeDeclaration.class, 11)
            .put(UnionTypeDeclaration.class, 12)
            .put(FileTypeDeclaration.class, 13)
            .put(AnyTypeDeclaration.class, 14)
            .build();
    }

    public static List<TypeDeclaration> sortByPriority(final List<TypeDeclaration> types) {
        // we need to sort types for best deserialization results (int before number, date before string, ...)
        List<TypeDeclaration> sortedTypes = new LinkedList<TypeDeclaration>(types);
        Map<Class<? extends TypeDeclaration>, Integer> typePriority = getPriorityTypeMap();
        Collections.sort(sortedTypes,(t1, t2) -> {
                // if both types are objects, we first do discriminator objects
                if (t1 instanceof ObjectTypeDeclaration && t2 instanceof ObjectTypeDeclaration) {
                    String d1 = ((ObjectTypeDeclaration) t1).discriminator();
                    String d2 = ((ObjectTypeDeclaration) t2).discriminator();
                    return d1 != null && d2 != null ? 0 : (d2 == null ? -1 : (d1 == null ? 1 : 0));
                }
                // no furhter process needed for other types
                return Integer.compare(typePriority.get(Utils.declarationType(t1)), typePriority.get(Utils.declarationType(t2)));
            }
        );
        return sortedTypes;
    }

    public static boolean isAmbiguous(List<TypeDeclaration> typeDeclarations, Function<TypeDeclaration, TypeName> converter) {
        Set<TypeName> types = new HashSet<>();
        for (TypeDeclaration typeDeclaration : typeDeclarations) {
            if (!types.add(converter.apply(typeDeclaration))) {
                return true;
            }
        }
        return false;
    }
}
