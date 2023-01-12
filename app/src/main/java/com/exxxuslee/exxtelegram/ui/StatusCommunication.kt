package com.exxxuslee.exxtelegram.ui

import com.exxxuslee.exxtelegram.core.Communication
import com.exxxuslee.exxtelegram.core.SingleLiveEvent
import org.drinkless.td.libcore.telegram.TdApi

class StatusCommunication {
    interface Put : Communication.Put<String>
    interface Post : Communication.Post<String>
    interface Observe : Communication.Observe<String>
    interface Value : Communication.Value<String>

    interface Mutable : Put, Observe, Post, Value

    class Base : Communication.Abstract<String>(SingleLiveEvent()), Mutable
}
