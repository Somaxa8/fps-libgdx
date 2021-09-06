package com.somacode.fps

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
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
    private lateinit var modelInstance: ModelInstance
    private lateinit var model: Model


    override fun create() {
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()
        camera = Camera(width, height)

        // Create Scene
        sceneAsset = GLTFLoader().load(Gdx.files.internal("quake.gltf"))
        scene = Scene(sceneAsset.scene)

        // Model Builder (Create Box)
        val mb = ModelBuilder()
        model = mb.createCylinder(4f, 8f, 4f, 16, Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position.toLong() or VertexAttributes.Usage.Normal.toLong())
        modelInstance = ModelInstance(model)
        modelInstance.transform.setToTranslation(18f, 0f, -100f)

        // Setup Light
        light = DirectionalLightEx()
        light.direction.set(2f, -3f, 4f).nor()
        light.color.set(Color.WHITE)

        // Add Scenes
        sceneManager = SceneManager()
        //Light
        sceneManager.setAmbientLight(0.00f)
        sceneManager.environment.add(light)
        //Scene
        sceneManager.addScene(scene)
        sceneManager.renderableProviders.add(modelInstance)
        //Camera
        sceneManager.setCamera(camera.perspectiveCamera)


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

        camera.perspectiveCamera.update()
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