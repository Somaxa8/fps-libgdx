package com.somacode.fps

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import net.mgsx.gltf.loaders.glb.GLBLoader
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx
import net.mgsx.gltf.scene3d.scene.Scene
import net.mgsx.gltf.scene3d.scene.SceneAsset
import net.mgsx.gltf.scene3d.scene.SceneManager
import kotlin.math.min


class Game : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private lateinit var pointer: Sprite
    private lateinit var camera: Camera
    private lateinit var inputManager: InputManager
    private lateinit var sceneManager: SceneManager
    private lateinit var sceneAsset: SceneAsset
    private lateinit var scene: Scene
    private lateinit var light: DirectionalLightEx


    override fun create() {
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()
        camera = Camera(width, height)

        // Create Scene
        sceneAsset = GLBLoader().load(Gdx.files.internal("quake.glb"))
        scene = Scene(sceneAsset.scene)

        // Setup Light
        light = DirectionalLightEx()
        light.direction.set(1f, -3f, 1f).nor()
        light.color.set(Color.WHITE)

        // Add Scenes
        sceneManager = SceneManager()
        sceneManager.environment.add(light)
        sceneManager.addScene(scene)

        // Sprite Pointer
        pointer = Sprite(Texture(Gdx.files.internal("pointer.png")))
        val pointerX: Float = width / 2 - pointer.width / 2
        val pointerY: Float = height / 2 - pointer.height / 2
        pointer.setPosition(pointerX, pointerY)

        batch = SpriteBatch()

        // Input Processor
        inputManager = InputManager(camera)
        Gdx.input.inputProcessor = inputManager
        Gdx.input.isCursorCatched = true
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }

        val delta: Float = min(1f/30f, Gdx.graphics.deltaTime)

        inputManager.update(delta)

        sceneManager.update(delta)
        sceneManager.render()

        batch.begin()
        pointer.draw(batch)
        batch.end()
    }

    override fun dispose() {
        sceneManager.dispose()
        sceneAsset.dispose()
    }

}