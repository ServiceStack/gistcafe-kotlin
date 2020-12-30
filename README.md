Useful utils for [gist.cafe](https://gist.cafe) Kotlin Apps.

## Usage

Simple usage example:

```kotlin
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.net.URL
import net.servicestack.gistcafe.*

data class GithubRepo(
    val name: String,
    val description: String = "",
    val homepage: String = "",
    @SerializedName("language") val lang: String = "",
    val watchers: Int,
    val forks: Int)

fun main(args: Array<String>) {
    val orgName = "Kotlin"

    val json = URL("https://api.github.com/orgs/$orgName/repos").readText()
    val orgRepos = Gson().fromJson<List<GithubRepo>>(json)
        .sortedByDescending { it.watchers }

    println("Top 3 $orgName GitHub Repos:")
    Inspect.printDump(orgRepos.take(3))

    println("\nTop 10 $orgName GitHub Repos:")
    Inspect.printDumpTable(orgRepos.take(10), listOf("name","language","watchers","forks"))

    Inspect.vars(mapOf("orgRepos" to orgRepos))
}
```

Which outputs:

```
Top 3 Kotlin GitHub Repos:
[
  {
    name: anko,
    description: Pleasant Android application development,
    homepage: ,
    language: Kotlin,
    watchers: 15892,
    forks: 1281
  },
  {
    name: kotlinx.coroutines,
    description: Library support for Kotlin coroutines ,
    homepage: ,
    language: Kotlin,
    watchers: 8684,
    forks: 1228
  },
  {
    name: kotlin-examples,
    description: Various examples for Kotlin,
    language: Kotlin,
    watchers: 2892,
    forks: 1056
  }
]

Top 10 Kotlin GitHub Repos:
+--------------------------------------------------------+
|          name           | language | watchers | forks  |
|--------------------------------------------------------|
| anko                    | Kotlin   |    15892 |   1281 |
| kotlinx.coroutines      | Kotlin   |     8684 |   1228 |
| kotlin-examples         | Kotlin   |     2892 |   1056 |
| kotlinx.serialization   | Kotlin   |     2697 |    287 |
| kotlin-koans            | Kotlin   |     2559 |   1504 |
| KEEP                    |          |     1953 |    215 |
| dokka                   | Kotlin   |     1839 |    194 |
| coroutines-examples     |          |     1259 |    149 |
| kotlin-fullstack-sample | Kotlin   |     1185 |    165 |
| kotlinx.html            | Kotlin   |     1074 |     97 |
+--------------------------------------------------------+
```

## Features and bugs

Please file feature requests and bugs at the [issue tracker](https://github.com/ServiceStack/gistcafe-kotlin/issues).