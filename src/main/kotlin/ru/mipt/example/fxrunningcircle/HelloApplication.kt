package ru.mipt.example.fxrunningcircle

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

private const val DV = 1.0

class RunningShape(val scope: CoroutineScope, val parent: Pane, x: Double, y: Double, color: Color) {

    private val scene get() = parent.scene
    val shape = Circle(10.0, color).also { shape ->
        parent.children.add(shape)
        shape.centerX = x
        shape.centerY = y
    }


    var vx = 0.0
    var vy = 0.0

    val updateJob = scope.launch {
        while (true) {
            Platform.runLater {
                shape.centerX += vx
                shape.centerY += vy

                if (shape.centerX < 0) {
                    shape.centerX = scene.width + shape.centerX
                }
                if (shape.centerX > scene.width) {
                    shape.centerX = shape.centerX - scene.width
                }

                if (shape.centerY < 0) {
                    shape.centerY = scene.height + shape.centerY
                }
                if (shape.centerY > scene.height) {
                    shape.centerY = shape.centerY - scene.height
                }
            }
            vx = if (vx >= 0) {
                max(vx - DV, 0.0)
            } else {
                min(vx + DV, 0.0)
            }
            vy = if (vy >= 0) {
                max(vy - DV, 0.0)
            } else {
                min(vy + DV, 0.0)
            }
            delay(50)
        }
    }
}


class HelloApplication : Application() {
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun start(stage: Stage) {
        //val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("hello-view.fxml"))
        val root: Pane = Pane()

        val scene = Scene(root, 320.0, 240.0)

        val circles = (0 until 80).map {
            RunningShape(
                scope,
                root,
                Random.nextDouble(0.0, scene.width),
                Random.nextDouble(0.0, scene.height),
                Color.color(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())
            )
        }

        scene.setOnMouseMoved { event ->
            circles.forEach {
                val circle = it.shape
                val distanceX = event.x - circle.centerX
                val distanceY = event.y - circle.centerY

                if (distanceX.pow(2) + distanceY.pow(2) < circle.radius.pow(2)) {
                    it.vx = -distanceX * 2
                    it.vy = -distanceY * 2
                }
            }
        }

        stage.title = "Hello!"
        stage.scene = scene
        stage.show()
    }

    override fun stop() {
        scope.cancel()
        super.stop()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}