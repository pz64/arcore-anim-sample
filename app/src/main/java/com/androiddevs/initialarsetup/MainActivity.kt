package com.androiddevs.initialarsetup

import android.animation.TimeInterpolator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.CycleInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.SkeletonNode
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.CompletableFuture

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "`MainActivity"
    }

    private lateinit var arFragment: ArFragment

    private var animator: ModelAnimator? = null
    private var renderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = fragment as ArFragment



        buttonAnim1.setOnClickListener {
            playAnimation("andy_wave_r")
        }

        buttonAnim2.setOnClickListener {
            playAnimation("andy_wave_l")
        }

        setupARPlaneTapListener()

    }

    private fun playAnimation(name:String) {

        animator?.let {
            if (it.isRunning)
                it.end()
        }

        renderable?.let {
            val animationData = it.getAnimationData(name)
            animator = ModelAnimator(animationData, renderable)
            animator?.start()
        }
    }

    private fun loadModel(callback: (ModelRenderable) -> Unit)  {
        ModelRenderable.builder()
            .setSource(this,R.raw.andy_dance)
            .build()
            .thenAccept {
                renderable = it
                callback(it)
            }
    }

    private fun addNodeToScene(anchor:Anchor, modelRenderable: ModelRenderable)   {
        val anchorNode = AnchorNode(anchor)

        val skeletalNode = SkeletonNode()
        skeletalNode.renderable = modelRenderable
        skeletalNode.setParent(anchorNode)


        val node = TransformableNode(arFragment.transformationSystem)
        node.addChild(skeletalNode)
        node.setParent(anchorNode)

        getCurrentScene().addChild(anchorNode)
    }

    private fun getCurrentScene() = arFragment.arSceneView.scene

    private fun setupARPlaneTapListener()   {
        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            loadModel { renderable ->
                addNodeToScene(hitResult.createAnchor(), renderable)
            }
        }
    }
}