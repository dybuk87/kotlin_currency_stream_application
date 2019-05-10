package pl.dybuk.currencytest.core.service.currency.impl

import android.content.Context
import android.os.Environment
import android.util.Log
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.io.FileWriter

sealed class Record(val raw: String) {

    data class Error(var rawData: String) : Record(rawData)

    data class Data(var rawData: String) : Record(rawData)
}

class Recorder {

    val data: BehaviorSubject<Record> = BehaviorSubject.create()

    val file = File(Environment.getExternalStorageDirectory(), "RECORD_" + System.currentTimeMillis() + ".txt")

    init {
        val subscribe = data
            .subscribeOn(Schedulers.io())
            .subscribe(this::saveData)
    }

    private fun saveData(record: Record) {
        Log.i("RECORD", record.raw)



        try {
            val fileWriter = FileWriter(file, true)

            fileWriter.use {
                when (record) {
                    is Record.Data -> fileWriter.write("D:${record.rawData}\n")
                    is Record.Error -> fileWriter.write("E:${record.rawData}\n")
                }

                fileWriter.close()
            }
        } catch (e : Throwable) {
            Log.e("RECORD", e.localizedMessage, e)
        }

    }

}