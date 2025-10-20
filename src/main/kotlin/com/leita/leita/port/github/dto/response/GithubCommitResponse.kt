package com.leita.leita.port.github.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubCommitResponse(
    val content: Content,
    val commit: Commit
) {
    data class Content(
        val name: String,
        val path: String,
        val sha: String,
        val size: Int,
        val url: String,
        @JsonProperty("html_url")
        val htmlUrl: String,
        @JsonProperty("git_url")
        val gitUrl: String,
        @JsonProperty("download_url")
        val downloadUrl: String,
        val type: String,
        @JsonProperty("_links")
        val links: Links
    ) {
        data class Links(
            val self: String,
            val git: String,
            val html: String
        )
    }

    data class Commit(
        val sha: String,
        @JsonProperty("node_id")
        val nodeId: String,
        val url: String,
        @JsonProperty("html_url")
        val htmlUrl: String,
        val author: Author,
        val committer: Committer,
        val message: String,
        val tree: Tree,
        val parents: List<Parent>,
        val verification: Verification
    ) {
        data class Author(
            val name: String,
            val email: String,
            val date: String
        )

        data class Committer(
            val name: String,
            val email: String,
            val date: String
        )

        data class Tree(
            val sha: String,
            val url: String
        )

        data class Parent(
            val sha: String,
            val url: String,
            @JsonProperty("html_url")
            val htmlUrl: String
        )

        data class Verification(
            val verified: Boolean,
            val reason: String,
            val signature: String?,
            val payload: String?
        )
    }
}






