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
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import net.mgsx.gltf.loaders.gltf.GLTFLoader
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
    private lateinit var playerInstance: ModelInstance
    private lateinit var playerModel: Model

    private lateinit var playerShape: btCollisionShape
    private lateinit var mapShape: btCollisionShape
    private lateinit var playerObject: btCollisionObject
    private lateinit var mapObject: btCollisionObject
    private lateinit var collisionConfig: btCollisionConfiguration
    private lateinit var dispatcher: btDispatcher
    private var collision: Boolean = false


    override fun create() {
        Bullet.init()

        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()
        camera = Camera(width, height)

        // Create Scene
        sceneAsset = GLTFLoader().load(Gdx.files.internal("quake.gltf"))
        scene = Scene(sceneAsset.scene)

        // Model Builder (Create Box)
        val mb = ModelBuilder()
        playerModel = mb.createCylinder(4f, 8f, 4f, 16, Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position.toLong() or VertexAttributes.Usage.Normal.toLong())
        playerInstance = ModelInstance(playerModel)
        playerInstance.transform.setToTranslation(18f, 25f, -100f)

        // Bullet
//        playerShape = btBoxShape(Vector3(2.5f, 0.5f, 2.5f))
        playerShape = btSphereShape(0.5f)
        playerObject = btCollisionObject()
//        playerObject.setCustomDebugColor()
        playerObject.collisionShape = playerShape
        playerObject.worldTransform = playerInstance.transform

        mapObject = btCollisionObject()
        mapObject.collisionShape = Bullet.obtainStaticNodeShape(scene.modelInstance.nodes)
        mapObject.worldTransform = scene.modelInstance.transform

        collisionConfig = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfig)

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
        sceneManager.renderableProviders.add(playerInstance)
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

    fun checkCollision(): Boolean {
        val co0 = CollisionObjectWrapper(playerObject)
        val co1 = CollisionObjectWrapper(mapObject)

        val ci = btCollisionAlgorithmConstructionInfo()
        ci.dispatcher1 = dispatcher
        val algorithm = btSphereBoxCollisionAlgorithm(null, ci, co0.wrapper, co1.wrapper, false)

        val info = btDispatcherInfo()
        val result = btManifoldResult(co0.wrapper, co1.wrapper)

        algorithm.processCollision(co0.wrapper, co1.wrapper, info, result)

        val r = result.persistentManifold.numContacts > 0

        result.dispose()
        info.dispose()
        algorithm.dispose()
        ci.dispose()
        co1.dispose()
        co0.dispose()

        return r
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }

        val delta: Float = min(1f/30f, Gdx.graphics.deltaTime)

        if (!collision) {
            playerInstance.transform.translate(0f, -delta, 0f)
            playerObject.worldTransform = playerInstance.transform

            collision = checkCollision()
        }

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
        playerObject.dispose()
        playerShape.dispose()
        dispatcher.dispose()
        collisionConfig.dispose()
    }

}