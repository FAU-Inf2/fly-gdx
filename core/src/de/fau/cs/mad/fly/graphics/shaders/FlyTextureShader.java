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
//import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
//import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
//import com.badlogic.gdx.graphics.glutils.ShaderProgram;
//import com.badlogic.gdx.utils.GdxRuntimeException;
//
///**
// * Created by tschaei on 22.07.14.
// */
//public class FlyTextureShader implements Shader {
//
//	private ShaderProgram program;
//	private Camera camera;
//	private RenderContext context;
//    private Environment environment;
//	private int u_ProjViewTrans, u_worldTrans, u_specularColor, u_ambientLight, texture1, numDirLights, numPointLights;
//    private int[][] u_dirLights, u_pointLights;
//
//    public FlyTextureShader(Renderable renderable) {
//        this.environment = renderable.environment;
//        this.numDirLights = this.environment.directionalLights.size;
//        this.numPointLights = this.environment.pointLights.size;
//    }
//
//	@Override
//	public void init() {
//        String prefix = "";
//        if(numDirLights > 0) prefix += "#define numDirLights " + this.numDirLights + "\n";
//        if(numPointLights > 0) prefix += "#define numPointLights " + this.numPointLights + "\n";
//        String vert = prefix + Gdx.files.internal("shaders/vertex.glsl").readString();
//		String frag = Gdx.files.internal("shaders/texture.fragment.glsl").readString();
//		program = new ShaderProgram(vert, frag);
//		if(!program.isCompiled()) {
//			throw new GdxRuntimeException(program.getLog());
//		}
//
//        u_dirLights = new int[numDirLights][2];
//        u_pointLights = new int[numPointLights][2];
//		u_ProjViewTrans = program.getUniformLocation("u_projViewTrans");
//		u_worldTrans = program.getUniformLocation("u_worldTrans");
//        u_specularColor = program.getUniformLocation("u_specularColor");
//        u_ambientLight = program.getUniformLocation("u_ambientLight");
//		texture1 = program.getUniformLocation("texture1");
//        for(int i=0; i<this.numDirLights; i++) {
//            u_dirLights[i][0] = program.getUniformLocation("u_dirLights[" + i + "].direction");
//            u_dirLights[i][1] = program.getUniformLocation("u_dirLights[" + i + "].color");
//        }
//        for(int i=0; i<this.numPointLights; i++) {
//            u_pointLights[i][0] = program.getUniformLocation("u_pointLights[" + i + "].position");
//            u_pointLights[i][1] = program.getUniformLocation("u_pointLights[" + i + "].color");
//        }
//	}
//
//	@Override
//	public int compareTo(Shader other) {
//		return 0;
//	}
//
//	@Override
//	public boolean canRender(Renderable instance) {
//		if(instance.environment != null && instance.material.has(TextureAttribute.Diffuse)) return true;
//		return false;
//	}
//
//	@Override
//	public void begin(Camera camera, RenderContext context) {
//		this.camera = camera;
//		this.context = context;
//		program.begin();
//		program.setUniformMatrix(u_ProjViewTrans, camera.combined);
//		context.setDepthTest(GL20.GL_DEPTH_TEST);
//		context.setCullFace(GL20.GL_BACK);
//	}
//
//	@Override
//	public void render(Renderable renderable) {
//		//Set uniforms
//        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
//        program.setUniformf(u_specularColor, ((ColorAttribute) renderable.material.get(ColorAttribute.Specular)).color);
//        if(renderable.environment.has(ColorAttribute.AmbientLight)) {
//            program.setUniformf(u_ambientLight, ((ColorAttribute)renderable.environment.get(ColorAttribute.AmbientLight)).color);
//        } else {
//            program.setUniformf(u_ambientLight, Color.BLACK);
//        }
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
//
//        //Bind texture
//		((TextureAttribute) renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture.bind(0);
//		program.setUniformi(texture1, 0);
//		renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
//	}
//
//	@Override
//	public void end() {
//		program.end();
//	}
//
//	@Override
//	public void dispose() {
//		program.dispose();
//	}
//}
package de.fau.cs.mad.fly.graphics.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class FlyTextureShader extends FlyBaseShader {

    private String VERTEX_SHADER = "shaders/vertex.glsl";
    private String FRAGMENT_SHADER = "shaders/texture.fragment.glsl";
    private int texture1;

    public FlyTextureShader(Renderable renderable) {
        super(renderable);
    }

    @Override
    public void init() {
        //Prepare and compile the ShaderProgram
        super.createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);

        super.init();

        texture1 = program.getUniformLocation("texture1");
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        if(instance.environment.equals(environment) && instance.material.has(TextureAttribute.Diffuse)) return true;
		return false;
    }

    @Override
    public void render(Renderable renderable) {
        //Set up uniforms
        super.setUpBaseUniforms(renderable);

        //Bind texture
		((TextureAttribute) renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture.bind(0);
		program.setUniformi(texture1, 0);
		renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize);
    }
}
