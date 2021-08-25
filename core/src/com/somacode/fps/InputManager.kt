package com.somacode.fps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.*
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.IntIntMap


class InputManager(val camera: Camera) : InputAdapter() {

    val keys = IntIntMap()
    var strafeLeftKey: Int = Keys.A
    var strafeRightKey: Int = Keys.D
    var forwardKey: Int = Keys.W
    var backwardKey: Int = Keys.S
    var upKey: Int = Keys.Q
    var downKey: Int = Keys.E
    var autoUpdate = true
    val velocity = 5f
    val degreesPerPixel = 0.5f
    val tmp = Vector3()

    override fun keyDown(keycode: Int): Boolean {
        keys.put(keycode, keycode)
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        keys.remove(keycode, 0)
        return true
    }

    fun update(deltaTime: Float) {
        if (keys.containsKey(forwardKey)) {
            tmp.set(camera.perspectiveCamera.direction).nor().scl(deltaTime * velocity)
            camera.perspectiveCamera.position.add(tmp)
        }
        if (keys.containsKey(backwardKey)) {
            tmp.set(camera.perspectiveCamera.direction).nor().scl(-deltaTime * velocity)
            camera.perspectiveCamera.position.add(tmp)
        }
        if (keys.containsKey(strafeLeftKey)) {
            tmp.set(camera.perspectiveCamera.direction).crs(camera.perspectiveCamera.up).nor().scl(-deltaTime * velocity)
            camera.perspectiveCamera.position.add(tmp)
        }
        if (keys.containsKey(strafeRightKey)) {
            tmp.set(camera.perspectiveCamera.direction).crs(camera.perspectiveCamera.up).nor().scl(deltaTime * velocity)
            camera.perspectiveCamera.position.add(tmp)
        }
        if (keys.containsKey(upKey)) {
            tmp.set(camera.perspectiveCamera.up).nor().scl(deltaTime * velocity)
            camera.perspectiveCamera.position.add(tmp)
        }
        if (keys.containsKey(downKey)) {
            tmp.set(camera.perspectiveCamera.up).nor().scl(-deltaTime * velocity)
            camera.perspectiveCamera.position.add(tmp)
        }
        if (autoUpdate) camera.perspectiveCamera.update(true)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val deltaX = -Gdx.input.deltaX * degreesPerPixel
        val deltaY = -Gdx.input.deltaY * degreesPerPixel
        camera.perspectiveCamera.direction.rotate(camera.perspectiveCamera.up, deltaX)
        tmp.set(camera.perspectiveCamera.direction).crs(camera.perspectiveCamera.up).nor()
        camera.perspectiveCamera.direction.rotate(tmp, deltaY)
        return true
    }

}