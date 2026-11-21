package io.github.jonathandelasen.remoto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/** Los tests de Gradle se ejecutan desde la carpeta del módulo (app/). */
class StringsTest {

    private data class Entry(val value: String, val translatable: Boolean)

    private fun read(path: String): Map<String, Entry> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(path))
        val nodes = doc.getElementsByTagName("string")
        return (0 until nodes.length).associate { i ->
            val el = nodes.item(i) as Element
            el.getAttribute("name") to Entry(el.textContent, el.getAttribute("translatable") != "false")
        }
    }

    private val english = read("src/main/res/values/strings.xml")
    private val spanish = read("src/main/res/values-es/strings.xml")

    @Test
    fun `todos los textos traducibles están en español`() {
        val translatable = english.filterValues { it.translatable }.keys
        assertEquals(translatable, spanish.keys)
    }

    @Test
    fun `ningún texto está vacío`() {
        for ((name, entry) in english + spanish) {
            assertTrue("$name está vacío", entry.value.isNotBlank())
        }
    }

    @Test
    fun `el nombre de la app no se traduce`() {
        assertEquals(Entry("Remoto", translatable = false), english["app_name"])
    }
}
