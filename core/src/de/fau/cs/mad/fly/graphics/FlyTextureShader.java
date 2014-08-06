package de.fau.cs.mad.fly.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by tschaei on 22.07.14.
 */
public class FlyTextureShader implements Shader {

	private ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	private int u_ProjViewTrans, u_worldTrans, texture1;

	@Override
	public void init() {
		String vert = Gdx.files.internal("shaders/vertex.glsl").readString();
		String frag = Gdx.files.internal("shaders/texture.fragment.glsl").readString();
		program = new ShaderProgram(vert, frag);
		if(!program.isCompiled()) {
			throw new GdxRuntimeException(program.getLog());
		}

		u_ProjViewTrans = program.getUniformLocation("u_projViewTrans");
		u_worldTrans = program.getUniformLocation("u_worldTrans");
		texture1 = program.getUniformLocation("texture1");
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		if(instance.material.get(TextureAttribute.Diffuse) != null) return true;
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
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		((TextureAttribute) renderable.material.get(TextureAttribute.Diffuse)).textureDescription.texture.bind(0);
		program.setUniformi(texture1, 0);
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
