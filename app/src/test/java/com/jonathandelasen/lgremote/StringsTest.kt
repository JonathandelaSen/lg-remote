package com.jonathandelasen.lgremote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/** Los tests de Gradle se ejecutan desde la carpeta del módulo (app/). */
class StringsTest {

    private data class Entry(val value: String, val translatable: Boolean)

    private val res = File("src/main/res")

    private fun read(file: File): Map<String, Entry> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val nodes = doc.getElementsByTagName("string")
        return (0 until nodes.length).associate { i ->
            val el = nodes.item(i) as Element
            el.getAttribute("name") to Entry(el.textContent, el.getAttribute("translatable") != "false")
        }
    }

    private val english = read(File(res, "values/strings.xml"))

    /** Carpeta `values-xx` → su fichero de textos. */
    private val translations: Map<String, File> = res.listFiles()!!
        .filter { it.name.startsWith("values-") && File(it, "strings.xml").exists() }
        .associate { it.name.removePrefix("values-") to File(it, "strings.xml") }

    @Test
    fun `están los 11 idiomas además del inglés`() {
        assertEquals(
            setOf("es", "fr", "de", "pt", "it", "ru", "ja", "zh-rCN", "hi", "ar", "in"),
            translations.keys,
        )
    }

    @Test
    fun `el APK incluye exactamente los idiomas traducidos`() {
        val gradle = File("build.gradle.kts").readText()
        val list = Regex("""localeFilters \+= listOf\(([^)]*)\)""").find(gradle)!!.groupValues[1]
        val filters = Regex("\"([^\"]+)\"").findAll(list).map { it.groupValues[1] }.toSet()
        assertEquals(translations.keys + "en", filters)
    }

    @Test
    fun `cada idioma traduce todos los textos y ninguno más`() {
        val translatable = english.filterValues { it.translatable }.keys
        for ((lang, file) in translations) {
            assertEquals("Idioma $lang", translatable, read(file).keys)
        }
    }

    @Test
    fun `ningún texto está vacío`() {
        for ((lang, file) in translations + ("en" to File(res, "values/strings.xml"))) {
            for ((name, entry) in read(file)) {
                assertTrue("$lang/$name está vacío", entry.value.isNotBlank())
            }
        }
    }

    @Test
    fun `los apóstrofos van escapados, o Android no compila`() {
        val unescaped = Regex("""(?<!\\)'""")
        for ((lang, file) in translations + ("en" to File(res, "values/strings.xml"))) {
            val body = file.readText().substringAfter("<resources>")
            assertFalse("$lang tiene un apóstrofo sin \\", unescaped.containsMatchIn(body))
        }
    }

    @Test
    fun `el nombre de la app no se traduce`() {
        assertEquals(Entry("LG Remote", translatable = false), english["app_name"])
    }
}
