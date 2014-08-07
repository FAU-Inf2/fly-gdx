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
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import de.fau.cs.mad.fly.game.GameController;

/**
 * Created by tschaei on 22.07.14.
 */
public class FlyShader implements Shader {

    private ShaderProgram program;
    private Camera camera;
    private RenderContext context;
    private int u_ProjViewTrans, u_worldTrans, u_diffuseColor, u_dirLightDirection, u_dirLightColor,
                u_normalMatrix, u_shininess, u_cameraPosition, u_specularColor, u_ambientColor;
    private Matrix3 normalMatrix;

    @Override
    public void init() {
        String vert = Gdx.files.internal("shaders/vertex.glsl").readString();
        String frag = Gdx.files.internal("shaders/fragment.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }

        normalMatrix = new Matrix3();

        //Store the uniform locations
        u_ProjViewTrans = program.getUniformLocation("u_projViewTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_ambientColor = program.getUniformLocation("u_ambientColor");
        u_diffuseColor = program.getUniformLocation("u_diffuseColor");
        u_specularColor = program.getUniformLocation("u_specularColor");
        u_dirLightDirection = program.getUniformLocation("u_dirLightDirection");
        u_dirLightColor = program.getUniformLocation("u_dirLightColor");
        u_normalMatrix = program.getUniformLocation("u_normalMatrix");
        u_shininess = program.getUniformLocation("u_shininess");
        u_cameraPosition = program.getUniformLocation("u_cameraPosition");
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
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        program.begin();
        program.setUniformMatrix(u_ProjViewTrans, camera.combined);
        context.setDepthTest(GL20.GL_DEPTH_TEST);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(Renderable renderable) {
        //Set all the uniforms for this renderable
        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
        if(renderable.environment.has(ColorAttribute.AmbientLight)) {
            program.setUniformf(u_ambientColor, ((ColorAttribute) renderable.environment.get(ColorAttribute.AmbientLight)).color);
        } else {
            program.setUniformf(u_ambientColor, Color.BLACK);
        }
        program.setUniformf(u_diffuseColor, ((ColorAttribute) renderable.material.get(ColorAttribute.Diffuse)).color);
        program.setUniformMatrix(u_normalMatrix, normalMatrix.set(renderable.worldTransform).inv().transpose());
        program.setUniformf(u_dirLightDirection, renderable.environment.directionalLights.get(0).direction);
        program.setUniformf(u_dirLightColor, renderable.environment.directionalLights.get(0).color);
        if (renderable.material.has(FloatAttribute.Shininess)) {
            program.setUniformf(u_shininess, ((FloatAttribute) renderable.material.get(FloatAttribute.Shininess)).value);
            program.setUniformf(u_specularColor, ((ColorAttribute) renderable.material.get(ColorAttribute.Specular)).color);
        } else {
            program.setUniformf(u_shininess, 0.0f);
            program.setUniformf(u_specularColor, Color.BLACK);
        }
        program.setUniformf(u_cameraPosition, this.camera.position);

        //Render the renderable
        renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
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
