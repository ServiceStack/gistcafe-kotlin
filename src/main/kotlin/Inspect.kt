import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileWriter
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.math.floor

class Inspect {

    companion object {

        @JvmStatic fun vars(args: Map<String, Any?>) {
            var inspectVarsPath : String = System.getenv("INSPECT_VARS") ?: return

            var writer: FileWriter? = null
            try {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    inspectVarsPath = inspectVarsPath.replace("/", "\\")
                } else {
                    inspectVarsPath = inspectVarsPath.replace("\\" , "/")
                }
                val dirPath = Paths.get(inspectVarsPath).parent
                Files.createDirectories(dirPath)

                writer = FileWriter(inspectVarsPath)

                Gson().toJson(args, writer)
            }
            finally {
                writer?.close()
            }
        }

        @JvmStatic inline fun <reified T> dump(obj: T): String {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val json = gson.toJson(obj)
            return json.replace("\"","")
        }

        @JvmStatic inline fun <reified T> printDump(obj: T) = println(dump(obj))

        @JvmStatic inline fun <reified T> dumpTable(objs: Iterable<T>, headers:Iterable<String>? = null): String {
            val rows = objs.toList()
            val mapRows = toListMap(rows)
            val keys = headers ?: allKeys(mapRows)
            val colSizes = HashMap<String,Int>()

            for (k in keys) {
                var max = k.length
                for (row in mapRows) {
                    if (row.containsKey(k)) {
                        val col = row[k]
                        val valSize = "$col".length
                        if (valSize > max) {
                            max = valSize
                        }
                    }
                }
                colSizes[k] = max
            }

            // sum + ' padding ' + |
            val rowWidth = colSizes.values.sum() +
                    (colSizes.size * 2) +
                    (colSizes.size + 1)

            val sb = StringBuilder()
            sb.appendLine("+${"-".repeat(rowWidth - 2)}+")

            sb.append("|")
            for (k in keys) {
                sb.append(alignCenter(k, colSizes[k]!!)).append("|")
            }
            sb.appendLine()
            sb.appendLine("|${"-".repeat(rowWidth - 2)}|")

            for (row in mapRows) {
                sb.append("|")
                for (k in keys) {
                    sb.append(alignAuto(row[k], colSizes[k]!!)).append("|")
                }
                sb.appendLine()
            }
            sb.appendLine("+${"-".repeat(rowWidth - 2)}+")

            return sb.toString()
        }

        @JvmStatic inline fun <reified T> printDumpTable(objs: Iterable<T>, headers:Iterable<String>? = null) = println(dumpTable(objs, headers))

        fun allKeys(rows: List<Map<String, Any?>>): List<String> {
            val to = ArrayList<String>()
            for (row in rows) {
                for (key in row.keys) {
                    if (!to.contains(key)) {
                        to.add(key)
                    }
                }
            }
            return to
        }

        @JvmStatic fun isNumber(obj: Any) : Boolean {
            return when(obj) {
                is Long -> true
                is Int -> true
                is Short -> true
                is Byte -> true
                is Double -> true
                is Float -> true
                else -> false
            }
        }

        private fun fmtNumber(d: Any): String {
            return when(d) {
                is Double -> if (d == floor(d)) d.toLong().toString() else d.toString()
                is Float -> if (d == floor(d)) d.toLong().toString() else d.toString()
                else -> d.toString()
            }
        }

        private fun alignLeft(str: String, len: Int, pad: String = " ") : String {
            if (len < 0) return "";
            val alen = len + 1 - str.length;
            if (alen <= 0) return str;
            return pad + str + (pad.repeat(len + 1 - str.length));
        }

        fun alignCenter(str: String, len: Int, pad: String = " ") : String {
            if (len < 0) return ""
            val nLen = str.length;
            val half = floor(len / 2.0 - nLen / 2.0).toInt();
            val odds = abs((nLen % 2) - (len % 2));
            return (pad.repeat(half + 1)) + str + (pad.repeat(half + 1 + odds))
        }

        private fun alignRight(str: String, len: Int, pad: String = " ") : String {
            if (len < 0) return ""
            val alen = len + 1 - str.length
            if (alen <= 0) return str
            return pad.repeat(len + 1 - str.length) + str + pad
        }

        fun alignAuto(obj: Any?, len: Int, pad: String = " ") : String {
            val str = if (obj == null) "" else "$obj"
            if (str.length <= len) {
                if (obj != null && isNumber(obj)) {
                    return alignRight(fmtNumber(obj), len, pad)
                }
                return alignLeft(str, len, pad)
            }
            return str;
        }

        @JvmStatic inline fun <reified T> toListMap(objs: List<T>): List<Map<String, Any?>> {
            val gson = Gson()
            val json = gson.toJson(objs)
            return gson.fromJson(json, object : TypeToken<List<Map<String, Any?>>>() {}.type)
        }

        @JvmStatic inline fun <reified T> toMap(obj: T): Map<String, Any?> {
            val gson = Gson()
            val json = gson.toJson(obj)
            return gson.fromJson(json, object : TypeToken<Map<String, Any?>>() {}.type)
        }
    }
}

inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, object: TypeToken<T>() {}.type)