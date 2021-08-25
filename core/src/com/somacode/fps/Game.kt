package com.somacode.fps

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.UBJsonReader


class Game : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private lateinit var pointer: Sprite
    private lateinit var modelBatch: ModelBatch
    private lateinit var map: ModelInstance
    private lateinit var ball: ModelInstance
    private lateinit var model: Model
    private lateinit var box: Model
    private var instances: MutableList<ModelInstance> = mutableListOf()
    private lateinit var camera: Camera
    private lateinit var environment: Environment
    private lateinit var inputManager: InputManager


    override fun create() {
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()
        camera = Camera(width, height)

        modelBatch = ModelBatch()

        // Environment
        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        val jsonReader = UBJsonReader()
        val modelLoader = G3dModelLoader(jsonReader)

        // Load 3d models
        model = modelLoader.loadModel(Gdx.files.internal("map.g3db"));
        map = ModelInstance(model)
        map.transform.setToTranslation(0f, 0f, 0f)

        // Model Builder (Create Box)
        val mb = ModelBuilder()
        box = mb.createBox(10f, 10f, 10f, Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position.toLong())
        ball = ModelInstance(box)
        ball.transform.setToTranslation(0f, 100f, 0f)

        // List instances
        instances.add(map)
        instances.add(ball)

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

        val delta: Float = Math.min(1f/30f, Gdx.graphics.deltaTime)

        //Camera position
//        val boxPosition = ball.transform.getTranslation(Vector3())
//        boxPosition.x = boxPosition.x + 5f
//        camera.perspectiveCamera.position.set(boxPosition)
        inputManager.update(delta)

        val direction = Vector2(camera.perspectiveCamera.direction.x, camera.perspectiveCamera.direction.y)

        modelBatch.begin(camera.perspectiveCamera)
        modelBatch.render(instances, environment)
        modelBatch.end()

        batch.begin();
        pointer.draw(batch);
        batch.end();
    }

    override fun dispose() {
        box.dispose();
        modelBatch.dispose();
        model.dispose();
    }

}