package ma.ensa.projet

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.ViewGroup
import android.widget.ProgressBar

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration du layout principal avec un dégradé animé
        val layout = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Animation du fond avec dégradé
            val colorAnim = ValueAnimator.ofObject(
                ArgbEvaluator(),
                Color.parseColor("#1A237E"),
                Color.parseColor("#311B92")
            ).apply {
                duration = 2000
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                addUpdateListener { animator ->
                    setBackgroundColor(animator.animatedValue as Int)
                }
            }
            colorAnim.start()
        }

        // Logo avec animations multiples
        val logoImageView = ImageView(this).apply {
            setImageResource(R.drawable.bio_icon)
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(200.dpToPx(), 200.dpToPx())

            // Animation de rotation 3D
            val rotateAnimation = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 2000
                interpolator = AccelerateDecelerateInterpolator()
                repeatCount = Animation.INFINITE
            }

            // Animation de pulsation
            val pulseAnimation = ScaleAnimation(
                0.8f, 1.2f, 0.8f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE
            }

            // Combinaison des animations
            val animationSet = AnimationSet(true).apply {
                addAnimation(rotateAnimation)
                addAnimation(pulseAnimation)
            }
            startAnimation(animationSet)
        }

        // Texte avec effet néon et animation de typing
        val textView = TextView(this).apply {
            textSize = 50f
            typeface = android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.BOLD)
            id = View.generateViewId()
            setShadowLayer(15f, 0f, 0f, Color.parseColor("#FF4081"))

            // Création d'un dégradé animé pour le texte
            val paint = paint
            val width = paint.measureText("CityScout")
            val textShader = LinearGradient(
                0f, 0f, width, textSize,
                intArrayOf(
                    Color.parseColor("#FF4081"),
                    Color.parseColor("#2196F3"),
                    Color.parseColor("#4CAF50")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            paint.shader = textShader
        }

        // ProgressBar personnalisée avec effet de vague
        val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            id = View.generateViewId()
            isIndeterminate = true
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                10.dpToPx()
            ).apply {
                leftMargin = 32.dpToPx()
                rightMargin = 32.dpToPx()
            }

            progressDrawable.setColorFilter(
                Color.parseColor("#FF4081"),
                PorterDuff.Mode.SRC_IN
            )

            // Animation de translation
            animate()
                .translationY(50f)
                .setDuration(1000)
                .setInterpolator(CycleInterpolator(0.5f))
                .withEndAction {
                    animate()
                        .translationY(0f)
                        .setDuration(1000)
                        .start()
                }
        }

        // Ajout des vues au layout
        layout.addView(logoImageView)
        layout.addView(textView)
        layout.addView(progressBar)
        setContentView(layout)

        // Configuration des contraintes
        ConstraintSet().apply {
            clone(layout)

            // Centrer le logo au milieu
            centerHorizontally(logoImageView.id, ConstraintSet.PARENT_ID)
            centerVertically(logoImageView.id, ConstraintSet.PARENT_ID)

            // Texte sous le logo
            centerHorizontally(textView.id, ConstraintSet.PARENT_ID)
            connect(textView.id, ConstraintSet.TOP, logoImageView.id, ConstraintSet.BOTTOM, 32)

            // ProgressBar sous le texte
            connect(progressBar.id, ConstraintSet.TOP, textView.id, ConstraintSet.BOTTOM, 32)
            centerHorizontally(progressBar.id, ConstraintSet.PARENT_ID)

            applyTo(layout)
        }

        // Animation du texte lettre par lettre avec effet de rebond
        val textToDisplay = "CityScout"
        var index = 0
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (index < textToDisplay.length) {
                    textView.text = textToDisplay.substring(0, index + 1)

                    // Animation de rebond pour chaque lettre
                    textView.scaleX = 1.5f
                    textView.scaleY = 1.5f
                    textView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(300)
                        .start()

                    index++
                    handler.postDelayed(this, 200)
                } else {
                    // Animation de sortie
                    handler.postDelayed({
                        layout.animate()
                            .alpha(0f)
                            .scaleX(1.5f)
                            .scaleY(1.5f)
                            .setInterpolator(AccelerateInterpolator())
                            .setDuration(800)
                            .withEndAction {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                finish()
                            }
                    }, 1000)
                }
            }
        }
        handler.post(runnable)
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
