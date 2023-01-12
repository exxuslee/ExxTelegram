package com.exxxuslee.exxtelegram

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exxxuslee.exxtelegram.core.Communication
import com.exxxuslee.exxtelegram.ui.MainCommunication
import com.exxxuslee.exxtelegram.ui.StatusCommunication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi.*


class MainViewModel : ViewModel(), Communication.Observe<List<Chat>>{
    private val communication: MainCommunication.Mutable = MainCommunication.Base()
    private val status: StatusCommunication.Mutable = StatusCommunication.Base()

    private lateinit var client: Client
    private lateinit var path: String
    private lateinit var phoneNumber: String
    private val defaultHandler = DefaultHandler()
    var onAuthorizedClickListener: ((Unit) -> Unit)? = null
    private var listChat = arrayListOf<Chat>()


    fun initClient(path: String, phoneNumber: String) {
        this.path = path
        this.phoneNumber = phoneNumber
        client = Client.create(UpdateHandler(), null, null)
    }

    fun createSecretChat(chatId: Int) {
        client.send(CreateSecretChat(chatId), defaultHandler)
    }

    fun chatList() {
        client.send(GetChats(null, 100), ChatRequestHandler())
    }

    fun sendCode(code: String) {
        client.send(CheckAuthenticationCode(code), AuthorizationRequestHandler())
    }

    fun sendPassword(password: String) {
        client.send(CheckAuthenticationPassword(password), AuthorizationRequestHandler())
    }

    fun sendMessage(chaiId: Long = -856295473L, message: String = "test") {
        val inputMessageText = InputMessageText(FormattedText(message, null), true, true)
        client.send(
            SendMessage(chaiId, 0, 0, null, null, inputMessageText),
            ChatRequestHandler()
        )
    }


    private fun onAuthorizationStateUpdated(authState: AuthorizationState?) {
        Log.d(TAG, "Got onAuthStateUpdated: ${authState?.javaClass?.canonicalName}")
        when (authState?.constructor) {
            AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> {
                val parameters = TdlibParameters()
                parameters.databaseDirectory = path
                parameters.useMessageDatabase = true
                parameters.useSecretChats = false
                parameters.apiId = 20701153
                parameters.apiHash = "707f3c539d03e6ef27f8e59a95466d1b"
                parameters.systemLanguageCode = "en"
                parameters.deviceModel = "mobile"
                parameters.applicationVersion = "1.0"
                parameters.enableStorageOptimizer = true
                //if (BuildConfig.DEBUG) parameters.useTestDc = true
                client.send(SetTdlibParameters(parameters), AuthorizationRequestHandler())
            }
            AuthorizationStateClosed.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateClosed")
            }
            AuthorizationStateClosing.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateClosing")
            }
            AuthorizationStateLoggingOut.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateLoggingOut")
            }
            AuthorizationStateReady.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateReady -> authorized")
                onAuthorizedClickListener?.invoke(Unit)
                status.post("AuthorizationStateReady")
            }
            AuthorizationStateWaitCode.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateWaitCode")
                status.post("AuthorizationStateWaitCode")
            }
            AuthorizationStateWaitEncryptionKey.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateWaitEncryptionKey")
                client.send(CheckDatabaseEncryptionKey(), AuthorizationRequestHandler())
            }
            AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR -> {
                Log.d(
                    TAG,
                    "AuthorizationStateWaitOtherDeviceConfirmation -> confirm link on another device"
                )
                status.post("AuthorizationStateWaitOtherDeviceConfirmation")
            }
            AuthorizationStateWaitPassword.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateWaitPassword -> please enter password")
                status.post("AuthorizationStateWaitPassword")
            }
            AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateWaitPhoneNumber -> please enter phone number")
                status.post("AuthorizationStateWaitPhoneNumber")
                client.send(
                    SetAuthenticationPhoneNumber(phoneNumber, null),
                    AuthorizationRequestHandler()
                )
            }
            AuthorizationStateWaitRegistration.CONSTRUCTOR -> {
                Log.d(TAG, "AuthorizationStateWaitRegistration -> please enter first and last name")

            }
        }
    }

    inner class AuthorizationRequestHandler : Client.ResultHandler {
        override fun onResult(result: Object) {
            when (result.constructor) {
                Error.CONSTRUCTOR -> {
                    Log.d(TAG, "Handler Authorization error")
                    onAuthorizationStateUpdated(null); // repeat last action
                }
                Ok.CONSTRUCTOR -> {
                    Log.d(TAG, "Handler Authorization OK")
                }
                else -> {
                    Log.d(TAG, "Handler Received wrong response from TDLib: ${result.constructor}")
                }
            }
        }

    }

    inner class ChatRequestHandler : Client.ResultHandler {
        override fun onResult(result: Object) {
            when (result.constructor) {
                Error.CONSTRUCTOR -> Log.d(TAG, "Handler Chat error - ${result.constructor}")
                Chats.CONSTRUCTOR -> {
                    Log.d(TAG, "Handler Chats OK")
                    onChatsUpdated((result as Chats))
                }
                Chat.CONSTRUCTOR -> {
                    Log.d(TAG, "Handler Chat OK")
                    onChatUpdated((result as Chat))
                }
                else -> {
                    Log.d(TAG, "Handler Received wrong response from TDLib: ${result.constructor}")
                }
            }

        }

    }

    private fun onChatsUpdated(chats: Chats) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                for (chat in chats.chatIds) client.send(GetChat(chat), ChatRequestHandler())
            }
        }
        communication.put(listChat)
    }

    private fun onChatUpdated(chat: Chat) {
        listChat.add(chat)
        Log.d(TAG, "${chat.id} - ${chat.title}")
        status.post("${chat.id} - ${chat.title}")
    }

    inner class UpdateHandler : Client.ResultHandler {
        override fun onResult(response: Object) {
            //Log.d(TAG, "Got UpdateHandler response: ${response.javaClass.canonicalName}")
            when (response.constructor) {
                UpdateAuthorizationState.CONSTRUCTOR -> {
                    onAuthorizationStateUpdated((response as UpdateAuthorizationState).authorizationState)
                }
                else -> throw IllegalArgumentException("Wrong type of menu")
            }

        }
    }

    inner class DefaultHandler : Client.ResultHandler {
        override fun onResult(result: Object?) {
            Log.d(TAG, "DefaultHandler response: ${result.toString()}")
        }
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<List<Chat>>) =
        communication.observe(owner, observer)

    companion object {
        private const val TAG = "tag"
    }

    fun observeStatus(owner: LifecycleOwner, observer: Observer<String>) =
        status.observe(owner, observer)

}