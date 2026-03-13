package com.example.healthgenieai.ui.diet

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

class DietFragment : Fragment() {

    private val API_KEY = BuildConfig.GEMINI_API_KEY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_diet, container, false)

        val age = view.findViewById<EditText>(R.id.etAge)
        val weight = view.findViewById<EditText>(R.id.etWeight)
        val height = view.findViewById<EditText>(R.id.etHeight)
        val spinner = view.findViewById<Spinner>(R.id.spGoal)
        val btn = view.findViewById<Button>(R.id.btnGenerateDiet)
        val result = view.findViewById<TextView>(R.id.tvDietResult)

        val goals = arrayOf(
            "Weight Loss",
            "Weight Gain",
            "Muscle Gain",
            "General Fitness"
        )

        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            goals
        )

        btn.setOnClickListener {

            val ageText = age.text.toString()
            val weightText = weight.text.toString()
            val heightText = height.text.toString()
            val goal = spinner.selectedItem.toString()

            if (ageText.isEmpty() || weightText.isEmpty() || heightText.isEmpty()) {
                Toast.makeText(requireContext(), "Fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prompt = """
You are a professional nutritionist AI.

User details:
Age: $ageText
Weight: $weightText kg
Height: $heightText cm
Goal: $goal

Create a healthy diet plan including:

Breakfast
Lunch
Dinner
Snacks
Hydration tips

Make it simple and healthy.
""".trimIndent()

            generateDiet(prompt, result)
        }

        return view
    }

    private fun generateDiet(prompt: String, result: TextView) {

        result.text = "Generating diet plan..."

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
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        client.newCall(request).enqueue(object : Callback {


            override fun onFailure(call: Call, e: IOException) {

                activity?.runOnUiThread {
                    result.text = "Error: ${e.message}"
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
                        result.text = text
                    }

                } catch (e: Exception) {

                    activity?.runOnUiThread {
                        result.text = "Parsing error\n$res"
                    }
                }
            }
        })
    }
}