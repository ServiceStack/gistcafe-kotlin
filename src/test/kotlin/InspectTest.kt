import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.junit.jupiter.api.Test
import java.net.URL

data class GithubRepo(
    val name: String,
    val description: String = "",
    val homepage: String = "",
    @SerializedName("language") val lang: String = "",
    val watchers: Int,
    val forks: Int)

class InspectTest {

    @Test
    fun `test Inspect`() {
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
}