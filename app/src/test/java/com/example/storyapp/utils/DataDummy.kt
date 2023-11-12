package com.example.storyapp.utils

import com.example.storyapp.data.local.entity.Story

object DataDummy {

    fun generateDummyStory() : List<Story> {
        val items : MutableList<Story> = arrayListOf()

        for(i in 0..100){
            val story = Story(
                id = "$i",
                photoUrl = "https://picsum.photos/200/300",
                name = "Name $i",
                description = "Description $i",
                lat = 0.0,
                lon = 0.0,
                createdAt = "2021-08-20T09:00:00.000Z",
            )
            items.add(story)
        }

        return items
    }
}