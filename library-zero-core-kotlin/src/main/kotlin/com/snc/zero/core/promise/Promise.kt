package com.snc.zero.core.promise

class Promise<T> {
    private var resolveCallback: ((T) -> Unit)? = null
    private var rejectCallback: ((Throwable) -> Unit)? = null
    private var finallyCallback: (() -> Unit)? = null

    fun resolve(value: T) {
        resolveCallback?.invoke(value)
        finallyCallback?.invoke()
    }

    fun reject(error: Throwable) {
        rejectCallback?.invoke(error)
        finallyCallback?.invoke()
    }

    fun then(onFulfilled: (T) -> Unit): Promise<T> {
        resolveCallback = onFulfilled
        return this
    }

    fun catch(onRejected: (Throwable) -> Unit): Promise<T> {
        rejectCallback = onRejected
        return this
    }

    fun finally(onFinally: () -> Unit): Promise<T> {
        finallyCallback = onFinally
        return this
    }

    companion object {
        fun <T> create(executor: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit): Promise<T> {
            val promise = Promise<T>()
            try {
                executor(promise::resolve, promise::reject)
            } catch (e: Throwable) {
                promise.reject(e)
            }
            return promise
        }
    }
}
