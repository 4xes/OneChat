package com.eor.onechat.data.model

class Data(
        val text: String? = null,
        val title: String? = null,
        val subtitle: String? = null) {
    companion object {
        fun full(): Data {
            return Data("Сколково", "Большой бульвар 42с1", "http://news.sfu-kras.ru/files/images/480-skolkovo.jpg")
        }

        fun text(): Data {
            return Data("Курс доллара падает")
        }
    }
}