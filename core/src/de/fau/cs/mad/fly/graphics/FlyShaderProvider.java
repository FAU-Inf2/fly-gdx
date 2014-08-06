package de.fau.cs.mad.fly.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

import java.util.ArrayList;

/**
 * Created by tschaei on 22.07.14.
 */
public class FlyShaderProvider extends BaseShaderProvider {
	@Override
	protected Shader createShader(Renderable renderable) {
		if(renderable.material.get(TextureAttribute.Diffuse) != null) {
			return new FlyTextureShader();
		}
		return new FlyShader();
	}
}
