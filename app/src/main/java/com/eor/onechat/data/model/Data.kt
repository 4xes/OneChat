package com.eor.onechat.data.model

class Data(
        val text: String,
        val title: String,
        val subtitle: String) {
    companion object {
        fun test(): Data {
            return Data("Сколково", "Большой бульвар 42с1", "http://news.sfu-kras.ru/files/images/480-skolkovo.jpg")
        }
    }
}