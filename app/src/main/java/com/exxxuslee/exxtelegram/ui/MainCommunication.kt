package com.exxxuslee.exxtelegram.ui

import com.exxxuslee.exxtelegram.core.Communication
import com.exxxuslee.exxtelegram.core.SingleLiveEvent
import org.drinkless.td.libcore.telegram.TdApi.Chat

interface MainCommunication {

    interface Put : Communication.Put<List<Chat>>
    interface Post : Communication.Post<List<Chat>>
    interface Observe : Communication.Observe<List<Chat>>
    interface Value : Communication.Value<List<Chat>>

    interface Mutable : Put, Observe, Post, Value

    class Base : Communication.Abstract<List<Chat>>(SingleLiveEvent()), Mutable
}