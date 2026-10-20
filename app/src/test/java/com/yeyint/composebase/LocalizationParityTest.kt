package com.yeyint.composebase

import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Locale key parity, enforced instead of remembered.
 *
 * Every translatable string in `res/values/` must exist in each `res/values-<lang>/` folder, with
 * no extras, and with the same format arguments. A missing key silently falls back to English; a
 * missing or extra `%1$s` crashes at runtime (`MissingFormatArgumentException`) or prints a stray
 * placeholder — and only in the language nobody on the team reads. The sibling apps' parity drifted
 * for exactly that reason.
 *
 * Locale folders are discovered, not listed, so adding `values-xx/` extends the check for free.
 * `translatable="false"` strings (pref keys, language names) are excluded from both sides.
 */
class LocalizationParityTest {

    private val resDir: File = listOf(File("src/main/res"), File("app/src/main/res")).first { it.isDirectory }

    private data class Entry(val kind: String, val name: String, val text: String)

    @Test
    fun `every locale has exactly the default locale's translatable keys`() {
        val default = entriesIn(resDir.resolve("values"))
        assertTrue("No translatable strings found under values/ — parse is broken.", default.isNotEmpty())

        val problems = localeDirs().flatMap { dir ->
            val localised = entriesIn(dir)
            val missing = default.keys - localised.keys
            val extra = localised.keys - default.keys
            missing.map { "${dir.name}: missing $it" } + extra.map { "${dir.name}: extra (no default) $it" }
        }
        assertTrue("Locale key parity broken:\n  " + problems.joinToString("\n  "), problems.isEmpty())
    }

    @Test
    fun `format arguments match across locales`() {
        val default = entriesIn(resDir.resolve("values"))
        val problems = localeDirs().flatMap { dir ->
            entriesIn(dir).mapNotNull { (key, entry) ->
                val reference = default[key] ?: return@mapNotNull null
                val expected = formatArgs(reference.text)
                val actual = formatArgs(entry.text)
                if (expected == actual) null
                else "${dir.name}: $key has ${actual.ifEmpty { listOf("<none>") }}, default has ${expected.ifEmpty { listOf("<none>") }}"
            }
        }
        assertTrue("Format-argument mismatch (runtime crash risk):\n  " + problems.joinToString("\n  "), problems.isEmpty())
    }

    private fun localeDirs(): List<File> =
        resDir.listFiles { f -> f.isDirectory && Regex("""values-[a-z]{2,3}(-r[A-Z]{2})?""").matches(f.name) }
            .orEmpty()
            .sortedBy { it.name }

    /** Translatable `<string>`/`<plurals>`/`<string-array>` entries across every XML in [dir], keyed by kind+name. */
    private fun entriesIn(dir: File): Map<String, Entry> {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val out = linkedMapOf<String, Entry>()
        dir.listFiles { f -> f.isFile && f.extension == "xml" }.orEmpty().sortedBy { it.name }.forEach { file ->
            val root = builder.parse(file).documentElement
            val children = root.childNodes
            for (i in 0 until children.length) {
                val node = children.item(i) as? Element ?: continue
                if (node.getAttribute("translatable") == "false") continue
                val kind = node.tagName
                if (kind != "string" && kind != "plurals" && kind != "string-array") continue
                val name = node.getAttribute("name")
                out["$kind:$name"] = Entry(kind, name, node.textContent)
            }
        }
        return out
    }

    /**
     * Sorted format specifiers in [text], `%%` excluded. Positional specifiers (`%2$s`) keep their
     * index, so a translation may reorder arguments but not drop, add or retype one.
     */
    private fun formatArgs(text: String): List<String> =
        Regex("""%(?:(\d+)\$)?[-#+ 0,(]*\d*(?:\.\d+)?([a-zA-Z])""")
            .findAll(text.replace("%%", ""))
            .map { (it.groupValues[1].ifEmpty { "?" }) + it.groupValues[2] }
            .toList()
            .sorted()
}
