package com.somacode.fps

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.somacode.fps.utils.Drawable
import com.somacode.fps.utils.Updatable


class Camera(width: Float, height: Float) : Drawable, Updatable {

    val perspectiveCamera: PerspectiveCamera = PerspectiveCamera(75f, width, height)

    init {
        perspectiveCamera.position.set(0f, 0f, 0f)
        perspectiveCamera.lookAt(0f, 0f, 0f)
        perspectiveCamera.near = 0.1f
        perspectiveCamera.far = 600f

    }

    override fun update(delta: Float) {
        perspectiveCamera.update();
    }

    override fun draw(modelBatch: ModelBatch) {
        modelBatch.begin(perspectiveCamera);
    }

}