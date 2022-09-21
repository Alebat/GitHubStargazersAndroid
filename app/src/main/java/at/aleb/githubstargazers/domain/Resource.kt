package at.aleb.githubstargazers.domain

sealed class Resource<T> {
    class START<T> : Resource<T>()
    class LOADING<T> : Resource<T>()
    data class SUCCESS<T>(val data: T) : Resource<T>()
    data class NOTFOUND<T>(val message: String = "") : Resource<T>()
    class NOCONNECTION<T> : Resource<T>()
    data class ERROR<T>(val code: Int) : Resource<T>()
}
