
package com.leita.leita.util.github.dto.response

data class InstallationRepositoriesResponse(
    val total_count: Int,
    val repository_selection: String,
    val repositories: List<Repository>
) {
    data class Repository(
        val id: Long,
        val name: String,
        val full_name: String,
        val description: String?,
        val html_url: String,
        val private: Boolean
    )
}