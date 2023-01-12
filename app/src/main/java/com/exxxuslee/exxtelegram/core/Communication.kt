package com.exxxuslee.exxtelegram.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

interface Communication {

    interface Put<T> {
        fun put(value: T)
    }

    interface Post<T> {
        fun post(value: T)
    }

    interface Observe<T> {
        fun observe(owner: LifecycleOwner, observer: Observer<T>)
    }

    interface Value<T> {
        fun value(): T
    }

    abstract class Abstract<T>(
        private val liveData: MutableLiveData<T> = MutableLiveData()
    ) : Put<T>, Observe<T>, Post<T>, Value<T> {

        override fun put(value: T) {
            liveData.value = value!!
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<T>) =
            liveData.observe(owner, observer)

        override fun post(value: T) {
            liveData.postValue(value!!)
        }

        override fun value(): T = liveData.value!!
    }
}