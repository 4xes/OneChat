package com.eor.onechat.data.model

class Places(val places: List<Place>) {
    companion object {
        fun test(): Places {
            val place1 = Place("Сколково", "Большой бульвар 42с1", "http://news.sfu-kras.ru/files/images/480-skolkovo.jpg")

            return Places(listOf(place1, place1))
        }
    }
}
