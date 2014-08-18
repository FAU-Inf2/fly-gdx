//package de.fau.cs.mad.fly.graphics;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Camera;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.g3d.Environment;
//import com.badlogic.gdx.graphics.g3d.Renderable;
//import com.badlogic.gdx.graphics.g3d.Shader;
//import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
//import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
//import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
//import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
//import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
//import com.badlogic.gdx.graphics.glutils.ShaderProgram;
//import com.badlogic.gdx.math.Matrix3;
//import com.badlogic.gdx.utils.GdxRuntimeException;
//
//import de.fau.cs.mad.fly.game.GameController;
//
///**
// * Created by tschaei on 22.07.14.
// */
//public class FlyShader implements Shader {
//
//    private ShaderProgram program;
//    private Camera camera;
//    private RenderContext context;
//    private int u_ProjViewTrans, u_worldTrans, u_diffuseColor, u_dirLightDirection, u_dirLightColor,
//                u_normalMatrix, u_shininess, u_cameraPosition, u_specularColor, u_ambientColor, numDirLights, numPointLights;
//    private int[][] u_dirLights, u_pointLights, u_modelViewMatrix;
//    private Matrix3 normalMatrix;
//    private Environment environment;
//
//    public FlyShader(Renderable renderable) {
//        this.environment = renderable.environment;
//        this.numDirLights = this.environment.directionalLights.size;
//        this.numPointLights = this.environment.pointLights.size;
//    }
//
//    @Override
//    public void init() {
//        String prefix = "";
//        if(numDirLights > 0) prefix += "#define numDirLights " + this.numDirLights + "\n";
//        if(numPointLights > 0) prefix += "#define numPointLights " + this.numPointLights + "\n";
//        String vert = Gdx.files.internal("shaders/vertex.glsl").readString();
//        String frag = prefix + Gdx.files.internal("shaders/fragment.glsl").readString();
//        program = new ShaderProgram(vert, frag);
//        if (!program.isCompiled()) {
//            throw new GdxRuntimeException(program.getLog());
//        }
//        u_dirLights = new int[this.numDirLights][2];
//        u_pointLights = new int[this.numPointLights][2];
//        normalMatrix = new Matrix3();
//
//        //Store the uniform locations
//        u_ProjViewTrans = program.getUniformLocation("u_projViewTrans");
//        u_worldTrans = program.getUniformLocation("u_worldTrans");
//        u_ambientColor = program.getUniformLocation("u_ambientColor");
//        u_diffuseColor = program.getUniformLocation("u_diffuseColor");
//        u_specularColor = program.getUniformLocation("u_specularColor");
//        u_dirLightDirection = program.getUniformLocation("u_dirLightDirection");
//        u_dirLightColor = program.getUniformLocation("u_dirLightColor");
//        u_normalMatrix = program.getUniformLocation("u_normalMatrix");
//        u_shininess = program.getUniformLocation("u_shininess");
//        u_cameraPosition = program.getUniformLocation("u_cameraPosition");
//        u_modelViewMatrix = program.getUniformLocation()
//        for(int i=0; i<this.numDirLights; i++) {
//            u_dirLights[i][0] = program.getUniformLocation("u_dirLights[" + i + "].direction");
//            u_dirLights[i][1] = program.getUniformLocation("u_dirLights[" + i + "].color");
//        }
//        for(int i=0; i<this.numPointLights; i++) {
//            u_pointLights[i][0] = program.getUniformLocation("u_pointLights[" + i + "].position");
//            u_pointLights[i][1] = program.getUniformLocation("u_pointLights[" + i + "].color");
//        }
//    }
//
//    @Override
//    public int compareTo(Shader other) {
//        return 0;
//    }
//
//    @Override
//    public boolean canRender(Renderable instance) {
//        if (instance.environment != null && !instance.material.has(TextureAttribute.Diffuse)) return true;
//        return false;
//    }
//
//    @Override
//    public void begin(Camera camera, RenderContext context) {
//        this.camera = camera;
//        this.context = context;
//        program.begin();
//        program.setUniformMatrix(u_ProjViewTrans, camera.combined);
//        context.setDepthTest(GL20.GL_DEPTH_TEST);
//        context.setCullFace(GL20.GL_BACK);
//    }
//
//    @Override
//    public void render(Renderable renderable) {
//        //Set all the uniforms for this renderable
//        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
//        if(renderable.environment.has(ColorAttribute.AmbientLight)) {
//            program.setUniformf(u_ambientColor, ((ColorAttribute) renderable.environment.get(ColorAttribute.AmbientLight)).color);
//        } else {
//            program.setUniformf(u_ambientColor, Color.BLACK);
//        }
//        program.setUniformf(u_diffuseColor, ((ColorAttribute) renderable.material.get(ColorAttribute.Diffuse)).color);
//        program.setUniformMatrix(u_normalMatrix, normalMatrix.set(renderable.worldTransform).inv().transpose());
////        program.setUniformf(u_dirLightDirection, renderable.environment.directionalLights.get(0).direction);
////        program.setUniformf(u_dirLightColor, renderable.environment.directionalLights.get(0).color);
//        if (renderable.material.has(FloatAttribute.Shininess)) {
//            program.setUniformf(u_shininess, ((FloatAttribute) renderable.material.get(FloatAttribute.Shininess)).value);
//            program.setUniformf(u_specularColor, ((ColorAttribute) renderable.material.get(ColorAttribute.Specular)).color);
//        } else {
//            program.setUniformf(u_shininess, 0.0f);
//            program.setUniformf(u_specularColor, Color.BLACK);
//        }
//        program.setUniformf(u_cameraPosition, this.camera.position);
//
//        for(int i=0; i<this.numDirLights; i++) {
//            program.setUniformf(u_dirLights[i][0], this.environment.directionalLights.get(i).direction);
//            program.setUniformf(u_dirLights[i][1], this.environment.directionalLights.get(i).color);
//        }
//
//        for(int i=0; i<this.numPointLights; i++) {
//            program.setUniformf(u_pointLights[i][0], this.environment.pointLights.get(i).position);
//            program.setUniformf(u_pointLights[i][1], this.environment.pointLights.get(i).color);
//        }
//
//        //Render the renderable
//        renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
//    }
//
//    @Override
//    public void end() {
//        program.end();
//    }
//
//    @Override
//    public void dispose() {
//        program.dispose();
//    }
//}
package de.fau.cs.mad.fly.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FlyShader extends FlyBaseShader {

    private int u_diffuseColor;

    public FlyShader(Renderable renderable) {
        super(renderable);
    }

    @Override
    public void init() {
        //Prepare and compile the ShaderProgram
        super.createShaderProgram(Gdx.files.internal("shaders/vertex.glsl").readString(), Gdx.files.internal("shaders/fragment.glsl").readString());
        super.init();

        Gdx.app.log("FlyShader.init", Integer.toString(numPointLights));

        u_diffuseColor = program.getUniformLocation("u_diffuseColor");
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        if (instance.environment != null && !instance.material.has(TextureAttribute.Diffuse)) return true;
        return false;
    }

    @Override
    public void render(Renderable renderable) {
        //Set up uniforms
        super.setUpBaseUniforms(renderable);
        program.setUniformf(u_diffuseColor, ((ColorAttribute) renderable.material.get(ColorAttribute.Diffuse)).color);

        //Render the renderable
        renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
    }
}