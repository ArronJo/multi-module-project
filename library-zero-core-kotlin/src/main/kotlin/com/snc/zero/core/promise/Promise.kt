package com.snc.zero.core.promise

class Promise<T> {
    private var resolveCallback: ((T) -> Unit)? = null
    private var rejectCallback: ((Throwable) -> Unit)? = null
    private var finallyCallback: (() -> Unit)? = null

    private var isResolved = false
    private var isRejected = false
    private var resolvedValue: T? = null
    private var rejectedError: Throwable? = null

    fun resolve(value: T) {
        if (isResolved || isRejected) return
        isResolved = true
        resolvedValue = value
        resolveCallback?.invoke(value)
        finallyCallback?.invoke()
    }

    fun reject(error: Throwable) {
        if (isResolved || isRejected) return
        isRejected = true
        rejectedError = error
        rejectCallback?.invoke(error)
        finallyCallback?.invoke()
    }

    fun then(onFulfilled: (T) -> Unit): Promise<T> {
        resolveCallback = onFulfilled
        if (isResolved) {
            resolvedValue?.let { onFulfilled(it) }
        }
        return this
    }

    fun catch(onRejected: (Throwable) -> Unit): Promise<T> {
        rejectCallback = onRejected
        if (isRejected) {
            rejectedError?.let { onRejected(it) }
        }
        return this
    }

    fun finally(onFinally: () -> Unit): Promise<T> {
        finallyCallback = onFinally
        if (isResolved || isRejected) {
            onFinally()
        }
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
