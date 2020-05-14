package com.example.githubreposapp.model

data class GithubComments(
    val body: String?,
    val id: String?
) {
    override fun toString() = "$body - $id"
}