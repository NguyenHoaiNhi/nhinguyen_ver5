package com.nhinguyen.translate


import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.*
import com.facebook.stetho.Stetho
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.nhinguyen.translate.ROOM.AppDatabase
import com.nhinguyen.translate.ROOM.Word
import com.nhinguyen.translate.ROOM.WordDAO
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    // Database handler
    lateinit var dao: WordDAO
    var words: ArrayList<Word> = ArrayList()
    var word_object = Word()

    // Spinner handler
    private lateinit var s1: Spinner
    private lateinit var s2: Spinner


    // Text to Speech
    var mTextToSpeech : TextToSpeech? = null
    var mResult = 0
    var mLocale = Locale("en")

    // Language handler
    var mLanguage1 = "en"
    var mLanguage2 = "vi"

    val mHandler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Stetho.initializeWithDefaults(this)

        // Init database
        initRoomDatabase()

        // Get all data from Room
        getWords()

        // Setup Spinner
        setupSpinner()

        // Save new word button
        ivSave.setOnClickListener{

            if(edEnglish.text.toString() != "" && edVietnam.text.toString() != "")
            {
                word_object.language1 =tvEnglish.text.toString()
                word_object.content_language1 = edEnglish.text.toString()
                word_object.language2 = tvVietnamese.text.toString()
                word_object.content_language2 = edVietnam.text.toString()

                // Default value
                val temp = Word()

                // Word is not exist & different with default value
                if((word_object != temp) && (wordAvailable(word_object) == false))
                {
                    dao.insert(word_object)
                    Toast.makeText(this@MainActivity, "Saved!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this@MainActivity, "This word was available!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Switch language button
        ivSwitch.setOnClickListener{
            var position1 = s2.selectedItemPosition
            var position2 = s1.selectedItemPosition
            s1.setSelection(position1)
            s2.setSelection(position2)
        }

        // Translate button
        btTranslate.setOnClickListener {
            mLanguage1 = when( s1.selectedItem.toString()){
                "English" -> "en"
                "Vietnamese" -> "vi"
                "Chinese"-> "zh"
                "Spanish" -> "es"
                "Korean"->"ko"
                "Japanese"->"ja"
                "French" -> "fr"
                "Italian"->"it"
                "Irish"->"ga"
                else -> {
                    "Nothing"
                }
            }
            mLanguage2 = when( s2.selectedItem.toString()){
                "English" -> "en"
                "Vietnamese" -> "vi"
                "Chinese"-> "zh"
                "Spanish" -> "es"
                "Korean"->"ko"
                "Japanese"->"ja"
                "French" -> "fr"
                "Italian"->"it"
                "Irish"->"ga"
                else -> {
                    "Nothing"
                }
            }
            var language = mLanguage1 + "-" + mLanguage2
            translation(language)
        }

        // Open save screen
        ivSaveScreen.setOnClickListener{
            val intent = Intent(this@MainActivity, SaveActivity::class.java)

            startActivity(intent)
        }

        // Camera button
        ivCamera.setOnClickListener{ goToCamera() }

        // Text to Speech handler
        mTextToSpeech = TextToSpeech(this,this)

        // Disable Mute button when start UI
        ibMuteVN.isEnabled = false
        ibMuteEN.isEnabled = false

        // Mute Language 1
        ibMuteEN.setOnClickListener {
            val str  = edEnglish.text.toString()

            // Get type of language
            mLocale = Locale(mLanguage1)
            Locale.setDefault(mLocale)

            // Set language
            mResult = mTextToSpeech!!.setLanguage(mLocale)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                mTextToSpeech!!.speak(str, TextToSpeech.QUEUE_FLUSH,null, null)
            }
        }

        // Mute Language 2
        ibMuteVN.setOnClickListener {
            val str  = edVietnam.text.toString()

            // Get type of language
            mLocale = Locale(mLanguage2)
            Locale.setDefault(mLocale)

            // Set language
            mResult = mTextToSpeech!!.setLanguage(mLocale)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                mTextToSpeech!!.speak(str, TextToSpeech.QUEUE_FLUSH,null, null)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS)
        {
            // Set locale
            Locale.setDefault(mLocale) // default is english ("en")
            mResult = mTextToSpeech!!.setLanguage(mLocale)

            if (mResult != TextToSpeech.LANG_MISSING_DATA || mResult != TextToSpeech.LANG_NOT_SUPPORTED)
            {
                // Enable mute button after init
                ibMuteEN.isEnabled = true
                ibMuteVN.isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        if (mTextToSpeech != null)
        {
            mTextToSpeech!!.stop()
            mTextToSpeech!!.shutdown()
        }
        super.onDestroy()
    }

    private fun wordAvailable(word: Word): Boolean {
        getWords()
        val size = words.size

        // Check item available
        if (size == 0)
        {
            return false
        }

        for (i in 0 until size)
        {
            if(words[i].content_language1 == word.content_language1
                && words[i].content_language2 == word.content_language2)
            {
                return true
            }
        }

        // List does not contain item
        return false
    }

    private fun getWords() {
        this.words.clear()
        val words = dao.getAll()
        this.words.addAll(words)
    }

    private fun goToCamera(){
        val intent = Intent(this, CamActivity::class.java)
        startActivity(intent)
    }
    private fun translation(lang: String){
        Log.i("translate", "Haha")
        var text = edEnglish.text
        var link= "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20190523T053835Z.0b61113b08a5ff52.f4eccc205a7332836a2a4a2444997625d673ad3a&lang="+lang+"&text="+text
        Log.i("Link: ", link.toString())
        okhttp(link)
    }

    fun okhttp( a: String){
        Log.i("Okhttp: ", "Haha")
        val client = OkHttpClient()
        val request = Request.Builder()
            .header("Authorization", "token abcd")
            .url(a)
            .build()
        client.newCall(request)
            .enqueue(object: Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.i("Fail", "No")
                    print("Fail load data")
                }
                override fun onResponse(call: Call, response: Response) {
                    Log.i("onResponse: ", "Haha")
                    if (response.isSuccessful){
                        Log.i("response: ", "HiHi")
                        var json = response.body()!!.string()
                        Log.i("JSON", json.toString())
                        var jsObect = JSONObject(json)
                        var result = jsObect.getJSONArray("text").toString()
                        var collectionType = object : TypeToken<Collection<String>>() {}.type
                        var word: ArrayList<String> = Gson().fromJson(result, collectionType)
                        Log.i("Data: ", word.toString())
                        mHandler.post(Runnable {
                            edVietnam.text = word.get(0).toString()
                        })

                    }
                }
            })

    }

    private fun setupSpinner(){
        val spinnerData = ArrayList<String>()

        spinnerData.add("English")
        spinnerData.add("Vietnamese")
        spinnerData.add("Chinese")
        spinnerData.add("Spanish")
        spinnerData.add("Korean")
        spinnerData.add("Japanese")
        spinnerData.add("French")
        spinnerData.add("Italian")
        spinnerData.add("Irish")

        s1 = findViewById(R.id.spinner1)
        s1.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerData)
        s1.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                tvEnglish.text = s1.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        s2 = findViewById(R.id.spinner2)
        s2.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerData)
        s2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    tvVietnamese.text = s2.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun initRoomDatabase(){
        val db  = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME).allowMainThreadQueries()
            .build()
         dao = db.wordDAO()
    }
}
