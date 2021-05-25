package com.btavares.comics.main.data.model

import com.btavares.comics.main.data.DataFixtures
import com.btavares.comics.main.domain.model.ComicDomainModel
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test



class ComicDataModelTest {



    @Test
    fun `convert comicDataModel to comicDomainModel`() {
        // given
        val dataModel = DataFixtures.getComicDataModelOne()


        //when
        val domainModel = dataModel.toDomainModel()


        //then
        domainModel shouldBeEqualTo ComicDomainModel(
            dataModel.month,
            dataModel.number,
            dataModel.link,
            dataModel.year,
            dataModel.news,
            dataModel.safeTitle,
            dataModel.description,
            dataModel.alt,
            dataModel.imageUrl,
            dataModel.title,
            dataModel.day,
            dataModel.isFavorite,
            comicBitmap = null
        )

    }

}