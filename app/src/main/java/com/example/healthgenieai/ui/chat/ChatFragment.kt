package com.example.healthgenieai.ui.chat

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.healthgenieai.BuildConfig
import com.example.healthgenieai.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatFragment : Fragment() {

    private val API_KEY = BuildConfig.GEMINI_API_KEY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val age = view.findViewById<EditText>(R.id.etAge)
        val temp = view.findViewById<EditText>(R.id.etTemp)
        val symptoms = view.findViewById<EditText>(R.id.etSymptoms)
        val btnCheck = view.findViewById<Button>(R.id.btnCheck)
        val result = view.findViewById<TextView>(R.id.tvResult)

        btnCheck.setOnClickListener {

            val ageText = age.text.toString().trim()
            val tempText = temp.text.toString().trim()
            val symptomsText = symptoms.text.toString().trim()

            if (ageText.isEmpty() || tempText.isEmpty() || symptomsText.isEmpty()) {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prompt = """
You are a medical assistant AI.

Patient Information:
Age: $ageText
Temperature: $tempText °C
Symptoms: $symptomsText

Provide:

1. Possible illness
2. Reason
3. Severity (Low / Moderate / High)
4. Whether hospital visit is needed
5. Home precautions

Disclaimer: This is not a medical diagnosis.
""".trimIndent()

            callGemini(prompt, result)
        }

        return view
    }

    private fun callGemini(prompt: String, resultView: TextView) {

        resultView.text = "Analyzing symptoms..."

        val part = JSONObject()
        part.put("text", prompt)

        val partsArray = JSONArray()
        partsArray.put(part)

        val content = JSONObject()
        content.put("parts", partsArray)

        val contentsArray = JSONArray()
        contentsArray.put(content)

        val json = JSONObject()
        json.put("contents", contentsArray)

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$API_KEY")
            .post(body)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                activity?.runOnUiThread {
                    resultView.text = "Network Error: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val res = response.body?.string()

                try {

                    val json = JSONObject(res)

                    val text = json
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    activity?.runOnUiThread {
                        resultView.text = text
                    }

                } catch (e: Exception) {

                    activity?.runOnUiThread {
                        resultView.text = "Parsing Error:\n$res"
                    }
                }
            }
        })
    }
}