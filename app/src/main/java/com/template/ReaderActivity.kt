package com.template

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.template.databinding.ActivityReaderBinding
import java.io.IOException
import kotlin.collections.ArrayList


class ReaderActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityReaderBinding

    private val pages: ArrayList<String> = ArrayList()
    private var currentPage = 0

    private lateinit var backButton: MaterialButton
    private lateinit var nextButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarReader.toolbar)
        initDrawer(binding.appBarReader.toolbar)

        listAssetFiles("pages")

        backButton = binding.appBarReader.contentReader.backButton
        backButton.setOnClickListener { back() }

        nextButton = binding.appBarReader.contentReader.nextButton
        nextButton.setOnClickListener { next() }

        loadCurrentPage()

        enableButtons()

        showPageInWebView()
    }

    /**
     * Устанавливает доступность кнопок Вперёд и Назад в зависимости от того, какая страница чейчас открыта
     */
    private fun enableButtons() {
        backButton.isEnabled = currentPage != 0
        nextButton.isEnabled = currentPage != pages.size - 1
    }

    /**
     * Достаёт файлы из папки assets и добавляет их в ArrayList pages
     */
    private fun listAssetFiles(path: String): Boolean {
        val list: Array<String>?
        try {
            list = assets.list(path)
            if (list!!.isNotEmpty()) {
                // Это папка
                for (file in list) {
                    if (!listAssetFiles("$path/$file")) return false else {
                        // Это файл
                        pages.add(file)
                    }
                }
            }
        } catch (e: IOException) {
            return false
        }
        return true
    }

    private fun initDrawer(toolbar: Toolbar) {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // Обработка навигационного меню
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            if (item.itemId == R.id.nav_share) {
                createShareIntent()
                drawer.closeDrawers()
                return@setNavigationItemSelectedListener true
            } else if (item.itemId == R.id.nav_rate) {
                createRateIntent()
                drawer.closeDrawers()
                return@setNavigationItemSelectedListener true
            }
            true
        }
    }

    /**
     * Поделиться
     */
    private fun createShareIntent() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(
            Intent.EXTRA_TEXT,
            packageName
        )
        intent.type = "text/plain"
        startActivity(intent)
    }

    /**
     * Оценить приложение
     */
    private fun createRateIntent() {
        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    /**
     * Отображение html-документов в WebView
     */
    private fun showPageInWebView() {
        val name = pages.get(currentPage)
        binding.appBarReader.contentReader.webView.loadUrl("file:///android_asset/pages/$name")
    }

    /**
     * Отображает следующую страницу
     */
    private fun next() {
        currentPage++
        enableButtons()
        showPageInWebView()
        saveCurrentPage()
    }

    /**
     * Отображает предыдущую страницу
     */
    private fun back() {
        currentPage--
        enableButtons()
        showPageInWebView()
        saveCurrentPage()
    }

    /**
     * Сохраняет номер текущей страницы в SharedPreferences
     */
    private fun saveCurrentPage() {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("page", currentPage)
        editor.apply()
    }

    /**
     * Загружает номер текущей страницы из SharedPreferences
     */
    private fun loadCurrentPage() {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        currentPage = sharedPreferences.getInt("page", 0)
    }
}