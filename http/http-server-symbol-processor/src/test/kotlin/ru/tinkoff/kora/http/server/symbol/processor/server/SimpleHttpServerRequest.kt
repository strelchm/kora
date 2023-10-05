package ru.tinkoff.kora.http.server.symbol.processor.server

import ru.tinkoff.kora.http.common.body.HttpBody
import ru.tinkoff.kora.http.common.body.HttpBodyInput
import ru.tinkoff.kora.http.common.header.HttpHeaders
import ru.tinkoff.kora.http.server.common.HttpServerRequest
import java.util.*

internal class SimpleHttpServerRequest(
    private val method: String,
    private val path: String,
    private val body: ByteArray,
    private val headers: Array<out Pair<String, String>>,
    private val routeParams: Map<String, String>
) : HttpServerRequest {
    override fun method(): String {
        return method
    }

    override fun path(): String {
        return path
    }

    override fun route(): String {
        return path
    }

    override fun headers(): HttpHeaders {
        val entries: Array<Map.Entry<String, List<String>>?> = arrayOfNulls(headers.size)
        for (i in headers.indices) {
            entries[i] = java.util.Map.entry(
                headers[i].first, listOf(
                headers[i].second
            )
            )
        }
        return HttpHeaders.of(*entries)
    }

    override fun queryParams(): Map<String, Deque<String>> {
        val questionMark = path.indexOf('?')
        if (questionMark < 0) {
            return mapOf()
        }
        val params = path.substring(questionMark + 1)
        val result = mutableMapOf<String, Deque<String>>()
        params.split("&".toRegex()).forEach { param ->
            val eq = param.indexOf('=')
            if (eq <= 0) {
                result[param] = ArrayDeque()
            } else {
                val name = param.substring(0, eq)
                val value = param.substring(eq + 1)
                result[name]?.add(value) ?: result.put(name, ArrayDeque<String>().apply { this.add(value) })
            }
        }
        return result
    }

    override fun pathParams(): Map<String, String> {
        return routeParams
    }

    override fun body(): HttpBodyInput {
        return HttpBody.octetStream(body)
    }
}
