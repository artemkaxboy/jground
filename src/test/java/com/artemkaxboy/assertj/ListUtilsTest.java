package com.artemkaxboy.assertj;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.MethodName.class)
class ListUtilsTest {

    /*
    We have some util classes to compare lists, but if one doesn't know their names, it's hard to find them.
    AssertJ on the other hand gives one a lot of methods to work with by autocompletion, all methods names are
    self-explanatory and well documented.
     */
    @Test
    void collectionTests_passes() {
        String element1 = "value1";
        String element2 = "value2";
        String unknownElement = "unknown";
        List<String> list = List.of(element1, element2);
        int listSize = list.size();
        List<String> copyList = List.copyOf(list);
        List<String> reverseList = ListUtils.reverse(list);
        List<String> subList = list.subList(0, 1);

        /* Variants of comparison with one element + chain of comparison */
        assertThat(list)
                // --------------------------------- contains ---------------------------------
                .contains(element1)                                // contains value
                .doesNotContain(unknownElement)                    // does not contain value

                .containsExactly(element1, element2)               // contains exactly the same elements in the same order
                .containsExactlyElementsOf(copyList)               // contains exactly the same elements in the same order
                .containsExactlyInAnyOrderElementsOf(reverseList)  // contains exactly the same elements in any order
                .containsExactlyInAnyOrder(element2, element1)     // contains exactly the same elements in any order

                .containsAnyElementsOf(subList)                    // contains any elements of collection
                .containsOnlyOnceElementsOf(copyList)              // contains only once elements of collection

                .containsSequence(element1, element2)              // contains sequence of elements
                .doesNotContainSequence(element2, element1)        // does not contain sequence of elements

                .doesNotHaveDuplicates()                           // does not have duplicates
                .doesNotContainNull()                              // does not contain null
//                .containsNull()                                    // contains null // just to show existence

//                .isEqualTo(reverseList)                            // is equal to collection // todo uncomment to see the error
                // --------------------------------- contains ---------------------------------

                // --------------------------------- types ---------------------------------
                .doesNotHaveAnyElementsOfTypes(Integer.class)      // does not have any elements of types
                .hasAtLeastOneElementOfType(String.class)          // has at least one element of type
                .hasOnlyElementsOfTypes(String.class, Long.class)  // has only elements of types
                // --------------------------------- types ---------------------------------

                // --------------------------------- sizes ---------------------------------
                .hasSize(listSize)                                 // has size
//                .hasSize(listSize - 1)                             // todo uncomment to see error
                .hasSameSizeAs(copyList)                           // has same size as collection
                .hasSizeBetween(listSize, listSize)                // has size between
                .hasSizeGreaterThanOrEqualTo(listSize)             // has size greater than or equal to
                .hasSizeLessThanOrEqualTo(listSize)                // has size less than or equal to
                // --------------------------------- sizes ---------------------------------

                // --------------------------------- elements ---------------------------------
                .allMatch(s -> s.startsWith("value"))              // all elements match condition (return true)
                .anyMatch(s -> s.endsWith("1"))                    // any element matches condition (return true)
                .allSatisfy(s -> {                                 // all elements satisfy condition (not throw exception)
                    assertThat(s).startsWith("value");
                    assertThat(s).matches(".*\\d");
//                    assertThat(s).matches(".*\\d2");               // todo uncomment to see error
                })
                .anySatisfy(s -> assertThat(s).endsWith("1"))      // any element satisfies condition (not throw exception)
                // --------------------------------- elements ---------------------------------

                .isNotNull();
    }
}
