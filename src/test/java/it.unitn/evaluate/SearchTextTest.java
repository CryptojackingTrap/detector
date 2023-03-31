package it.unitn.evaluate;

import it.unitn.deprecated.control.search.LCS;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.Collection;

public class SearchTextTest {
    public static void main(String[] args) {
        // testLcs();
        // testAhoCorasickInFile();
    }

    private static void testAhoCorasick() {
        Trie trie = Trie.builder()
                .ignoreOverlaps()
                .addKeyword("hers")
                .addKeyword("his")
                .addKeyword("she")
                .addKeyword("he")
                .build();
        Collection<Emit> emits = trie.parseText("ushershisshe");
        System.out.println(emits);
    }

    private static void testLcs() throws Exception {
        System.out.println(LCS.lcs("MilexxsxxxMxxxxMxxxxxxM", "xxxxxxxM"));
    }

}
