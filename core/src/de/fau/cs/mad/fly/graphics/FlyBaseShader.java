package de.fau.cs.mad.fly.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by tschaei on 13.08.14.
 */
public abstract class FlyBaseShader implements Shader{

    protected ShaderProgram program;
    private Environment environment;
    private Matrix3 normalMatrix;
    private Matrix4 modelViewMatrix, modelViewProjectionMatrix;
    protected int numDirLights, numPointLights;
    private int u_modelViewMatrix, u_modelMatrix, u_modelViewProjectionMatrix, u_shininess,
                u_ambientColor, u_specularColor, u_normalMatrix, u_cameraPosition,

    u_ProjViewTrans, u_worldTrans;
    protected int[][] u_dirLights, u_pointLights;
    protected Camera camera;
    private RenderContext context;

    public FlyBaseShader() {

    }

    public FlyBaseShader(Renderable renderable) {
        this.environment = renderable.environment;
        this.numDirLights = this.environment.directionalLights.size;
        this.numPointLights = this.environment.pointLights.size;
    }

    protected void createShaderProgram(String vertexShader, String fragmentShader) {
        //Prepare and compile the ShaderProgram
        String prefix = "";
        if(numDirLights > 0) prefix += "#define numDirLights " + this.numDirLights + "\n";
        if(numPointLights > 0) prefix += "#define numPointLights " + this.numPointLights + "\n";
        String vert = prefix + vertexShader;
        String frag = prefix + fragmentShader;
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }
    }

    @Override
    public void init() {
        u_dirLights = new int[this.numDirLights][2];
        u_pointLights = new int[this.numPointLights][2];
        normalMatrix = new Matrix3();
        modelViewMatrix = new Matrix4();
        modelViewProjectionMatrix = new Matrix4();

        //Save the uniform locations
        u_modelViewMatrix = program.getUniformLocation("u_modelViewMatrix");
        u_modelMatrix = program.getUniformLocation("u_modelMatrix");
        u_modelViewProjectionMatrix = program.getUniformLocation("u_modelViewProjectionMatrix");
        u_shininess = program.getUniformLocation("u_shininess");
        u_ambientColor = program.getUniformLocation("u_ambientColor");
        u_specularColor = program.getUniformLocation("u_specularColor");
        u_normalMatrix = program.getUniformLocation("u_normalMatrix");
        u_cameraPosition = program.getUniformLocation("u_cameraPosition");
        for(int i=0; i<this.numDirLights; i++) {
            u_dirLights[i][0] = program.getUniformLocation("u_dirLights[" + i + "].direction");
            u_dirLights[i][1] = program.getUniformLocation("u_dirLights[" + i + "].color");
        }
        for(int i=0; i<this.numPointLights; i++) {
            u_pointLights[i][0] = program.getUniformLocation("u_pointLights[" + i + "].position");
            u_pointLights[i][1] = program.getUniformLocation("u_pointLights[" + i + "].color");
        }



        u_ProjViewTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        program.begin();
        program.setUniformMatrix(u_ProjViewTrans, camera.combined);
        this.context.setDepthTest(GL20.GL_DEPTH_TEST);
        this.context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public boolean canRender(Renderable renderable) {
        return false;
    }

    protected void setUpBaseUniforms(Renderable renderable) {
        //Calculate the normal matrix
        normalMatrix.set(renderable.worldTransform).inv().transpose();

        //Pass the uniform values
        program.setUniformMatrix(u_normalMatrix, normalMatrix);
        program.setUniformMatrix(u_modelMatrix, renderable.worldTransform);
        program.setUniformMatrix(u_modelViewMatrix, modelViewMatrix.set(camera.view).mul(renderable.worldTransform));
        program.setUniformMatrix(u_modelViewProjectionMatrix, modelViewProjectionMatrix.set(camera.combined).mul(renderable.worldTransform));
        if (renderable.material.has(FloatAttribute.Shininess)) {
            program.setUniformf(u_shininess, ((FloatAttribute) renderable.material.get(FloatAttribute.Shininess)).value);
            program.setUniformf(u_specularColor, ((ColorAttribute) renderable.material.get(ColorAttribute.Specular)).color);
        } else {
            program.setUniformf(u_shininess, 0.0f);
            program.setUniformf(u_specularColor, Color.BLACK);
        }
        if(renderable.environment.has(ColorAttribute.AmbientLight)) {
            program.setUniformf(u_ambientColor, ((ColorAttribute) renderable.environment.get(ColorAttribute.AmbientLight)).color);
        } else {
            program.setUniformf(u_ambientColor, Color.BLACK);
        }
        for(int i=0; i<this.numDirLights; i++) {
            program.setUniformf(u_dirLights[i][0], this.environment.directionalLights.get(i).direction);
            program.setUniformf(u_dirLights[i][1], this.environment.directionalLights.get(i).color);
        }

        for(int i=0; i<this.numPointLights; i++) {
            program.setUniformf(u_pointLights[i][0], this.environment.pointLights.get(i).position);
            program.setUniformf(u_pointLights[i][1], this.environment.pointLights.get(i).color);
        }
        program.setUniformf(u_cameraPosition, camera.position);


        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }
}
