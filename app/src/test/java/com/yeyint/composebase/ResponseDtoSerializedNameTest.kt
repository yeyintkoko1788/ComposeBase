package com.yeyint.composebase

import com.google.gson.annotations.SerializedName
import com.yeyint.composebase.network.response.BaseResponse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier
import java.util.jar.JarFile

/**
 * Guards the single most repeated silent bug in the scaffold's sibling apps: a response DTO field
 * with no `@SerializedName`.
 *
 * The shared Gson uses `FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES` (see `NetworkModule`) but
 * the APIs are camelCase, so a field called `accessToken` is looked up as `access_token`, found
 * nowhere, and the "successful" response comes back with a null in it — no exception, no log.
 * One real instance: a session id read from the wrong JSON key was `""` for every signed-in user,
 * which silently turned a whole feature into a no-op until someone noticed in a network inspector.
 *
 * The rule is "any field whose name contains an uppercase letter must carry `@SerializedName`".
 * Single-word lower-case names (`meta`, `body`, `data`) are unaffected by the naming policy, so
 * they are exempt rather than forcing noise onto every wrapper class.
 */
class ResponseDtoSerializedNameTest {

    private val responsePackage = "com.yeyint.composebase.network.response"

    @Test
    fun `every camelCase response field has an explicit SerializedName`() {
        val classes = classesIn(responsePackage)
        // A scan that silently finds nothing would pass forever; fail loudly instead.
        assertTrue(
            "Found no classes in $responsePackage — the class scan is broken (package renamed?), " +
                "so this test would pass vacuously.",
            classes.isNotEmpty()
        )

        val offenders = classes.flatMap { cls ->
            cls.declaredFields
                .filter { !Modifier.isStatic(it.modifiers) && !it.isSynthetic }
                .filter { field -> field.name.any(Char::isUpperCase) }
                .filter { field -> field.getAnnotation(SerializedName::class.java) == null }
                .map { field -> "${cls.simpleName}.${field.name}" }
        }.sorted()

        assertTrue(
            "These DTO fields have no @SerializedName and the shared Gson will look them up in " +
                "lower_snake_case, reading null from a camelCase API:\n  " +
                offenders.joinToString("\n  "),
            offenders.isEmpty()
        )
    }

    /**
     * Every concrete class compiled into [pkg].
     *
     * Anchored on [BaseResponse]'s code source rather than `ClassLoader.getResources(packagePath)`:
     * AGP puts the app's classes on the unit-test classpath as a JAR with no explicit directory
     * entries, so asking the classloader for the package directory returns nothing and the scan
     * finds zero classes. The anchor must live in the app's own compiled output — if you move
     * `BaseResponse` out of [responsePackage], point this at another class from the same module.
     */
    private fun classesIn(pkg: String): List<Class<*>> {
        val loader = Thread.currentThread().contextClassLoader!!
        val prefix = pkg.replace('.', '/') + "/"
        val location = File(BaseResponse::class.java.protectionDomain.codeSource.location.toURI())

        val names: List<String> = if (location.isDirectory) {
            location.resolve(prefix).walkTopDown()
                .filter { it.isFile && it.extension == "class" }
                .map { it.relativeTo(location).path.replace(File.separatorChar, '/') }
                .toList()
        } else {
            JarFile(location).use { jar ->
                jar.entries().toList().map { it.name }.filter { it.startsWith(prefix) && it.endsWith(".class") }
            }
        }

        return names
            .map { Class.forName(it.removeSuffix(".class").replace('/', '.'), false, loader) }
            .filter { !it.isInterface && !it.isEnum && !it.isAnnotation && !it.isSynthetic }
    }
}
