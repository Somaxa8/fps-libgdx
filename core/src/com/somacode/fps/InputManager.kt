package com.somacode.fps

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3


class InputManager(val camera: Camera, val ball: ModelInstance) : InputProcessor {

    private var positionX = 100f
    private var positionZ = 100f
    private var positionY = 50f

    private var dragX = 0
    private var dragY: Int = 0
    private val rotateSpeed = 0.2f

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.LEFT) {
            positionX -= 100;
            ball.transform.setToTranslation(positionX, positionY, 0f)
        }
        if (keycode == Input.Keys.RIGHT) {
            positionX += 100;
            ball.transform.setToTranslation(positionX, positionY, 0f)
        }
        if (keycode == Input.Keys.UP) {
            positionZ -= 100;
            ball.transform.setToTranslation(positionX, positionY, positionZ)
        }
        if (keycode == Input.Keys.DOWN) {
            positionZ += 100;
            ball.transform.setToTranslation(positionX, positionY, positionZ)
        }
        if (keycode == Input.Keys.SPACE) {
            positionY += 50f;
            ball.transform.setToTranslation(positionX, positionY, positionZ)
        }
        if (keycode == Input.Keys.SHIFT_LEFT) {
            positionY -= 50f;
            ball.transform.setToTranslation(positionX, positionY, positionZ)
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val direction: Vector3 = camera.perspectiveCamera.direction.cpy()

        // rotating on the y axis
        val x = (dragX - screenX).toFloat()

        // change this Vector3.y with cam.up if you have a dynamic up.
        camera.perspectiveCamera.rotate(Vector3.Y, x * rotateSpeed)

        // rotating on the x and z axis is different
        val y = Math.sin((dragY - screenY).toDouble() / 180f).toFloat()
        if (Math.abs(camera.perspectiveCamera.direction.y + y * (rotateSpeed * 5.0f)) < 0.9) {
            camera.perspectiveCamera.direction.y += y * (rotateSpeed * 5.0f)
        }

        camera.perspectiveCamera.update()
        dragX = screenX
        dragY = screenY
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }
}