package com.example.mindcheckdementiaprobability

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mindcheckdementiaprobability.databinding.ActivityMainBinding

// Data class to hold calculated statistics.
data class DementiaStats(
    val probability: Double,    // Post-test probability of dementia.
    val prevalence: Double,     // Baseline prevalence for dementia.
    val likelihoodRatio: Double // Likelihood ratio for the MMSE result.
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.setOnClickListener {
            // Get gender selection.
            val gender = when (binding.rgGender.checkedRadioButtonId) {
                binding.rbFemale.id -> "Female"
                binding.rbMale.id -> "Male"
                else -> {
                    Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Get age category selection.
            // Note the updated binding references: rbAge60_64 becomes rbAge6064, etc.
            val ageCategory = when (binding.rgAgeCategory.checkedRadioButtonId) {
                binding.rbAge6064.id -> "60-64"
                binding.rbAge6569.id -> "65-69"
                binding.rbAge7074.id -> "70-74"
                binding.rbAge7579.id -> "75-79"
                binding.rbAge8084.id -> "80-84"
                binding.rbAge8589.id -> "85-89"
                binding.rbAge90plus.id -> "90+"
                else -> {
                    Toast.makeText(this, "Please select an age category", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Get MMSE result selection.
            val mmseRange = when (binding.rgMMSE.checkedRadioButtonId) {
                binding.rbMMSELow.id -> "MMSE 0-24"
                binding.rbMMSEHigh.id -> "MMSE 25-30"
                else -> {
                    Toast.makeText(this, "Please select an MMSE result", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Calculate statistics.
            val stats = calculateDementiaStats(gender, ageCategory, mmseRange)

            // Format and display the results.
            val resultText = """
                Prevalence for dementia: ${(stats.prevalence * 100).format(2)}%
                Likelihood Ratio for MMSE: ${stats.likelihoodRatio}
                Post-test Probability: ${(stats.probability * 100).format(2)}%
            """.trimIndent()

            binding.tvResult.text = resultText
        }
    }

    /**
     * Calculates the prevalence, likelihood ratio, and post-test probability for dementia.
     *
     * @param gender "Male" or "Female".
     * @param ageCategory The selected age group.
     * @param mmseRange "MMSE 0-24" or "MMSE 25-30".
     * @return A DementiaStats object containing the calculated values.
     */
    private fun calculateDementiaStats(gender: String, ageCategory: String, mmseRange: String): DementiaStats {
        // Baseline prevalence values by gender and age category.
        val prevalence = when (gender) {
            "Male" -> when (ageCategory) {
                "60-64" -> 0.014
                "65-69" -> 0.023
                "70-74" -> 0.037
                "75-79" -> 0.063
                "80-84" -> 0.106
                "85-89" -> 0.174
                "90+"   -> 0.334
                else    -> 0.0
            }
            "Female" -> when (ageCategory) {
                "60-64" -> 0.019
                "65-69" -> 0.03
                "70-74" -> 0.05
                "75-79" -> 0.086
                "80-84" -> 0.148
                "85-89" -> 0.274
                "90+"   -> 0.480
                else    -> 0.0
            }
            else -> 0.0
        }

        // Set the likelihood ratio based on MMSE result.
        val likelihoodRatio = if (mmseRange == "MMSE 0-24") 6.30 else 0.19

        // Calculate post-test probability using Bayes’ theorem:
        // preTestOdds = prevalence / (1 - prevalence)
        // postTestOdds = preTestOdds * likelihoodRatio
        // postTestProbability = postTestOdds / (1 + postTestOdds)
        val preTestOdds = if (prevalence < 1.0) prevalence / (1 - prevalence) else 0.0
        val postTestOdds = preTestOdds * likelihoodRatio
        val probability = postTestOdds / (1 + postTestOdds)

        return DementiaStats(probability, prevalence, likelihoodRatio)
    }

    // Extension function to format doubles.
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}
