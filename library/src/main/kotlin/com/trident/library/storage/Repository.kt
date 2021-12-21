package com.trident.library.storage

import androidx.lifecycle.LiveData
import com.trident.library.storage.persistroom.LinkDao
import com.trident.library.storage.persistroom.model.Link
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(var linkDao: LinkDao) {

    val readAllData: LiveData<List<Link>> = linkDao.getAll()


    fun getAllData(): List<Link>{
        return linkDao.getAllData()
    }

    fun insert(link: Link){
        GlobalScope.launch(Dispatchers.IO){ linkDao.addLink(link) }
    }

    fun updateLink(link: Link){
        GlobalScope.launch(Dispatchers.IO) { linkDao.updateLink(link)  }
    }
}